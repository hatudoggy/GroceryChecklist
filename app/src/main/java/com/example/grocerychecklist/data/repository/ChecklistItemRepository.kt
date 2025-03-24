package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.domain.utility.DateUtility
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

enum class ChecklistItemOrder(val order: String) {
    Order("order"),
    Name("name"),
    Price("price"),
    Date("createdAt"),
}

class ChecklistItemRepository(
    private val checklistItemDAO: ChecklistItemDAO,
    private val itemDAO: ItemDAO
) {

    private val db = FirebaseFirestore.getInstance()
    private val checklistItemsRef = db.collection("checklistItems")
    private val itemsRef = db.collection("items")

    suspend fun addChecklistItem(checklistId: Long, checklistItemInput: ChecklistItemInput): Long {
        val currentDateTime = DateUtility.getCurrentDateTime()

        // Create and save the item first
        val item = Item(
            name = checklistItemInput.name,
            price = checklistItemInput.price,
            category = checklistItemInput.category,
            measureType = checklistItemInput.measureType,
            measureValue = checklistItemInput.measureValue,
            photoRef = checklistItemInput.photoRef,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )
        val itemDocRef = itemsRef.add(item).await()
        val itemId = itemDocRef.id.toLong()

        // Get the max order for the new item
        val order = getMaxOrder(checklistId) + 1

        // Create and save the checklist item
        val checklistItem = ChecklistItem(
            checklistId = checklistId,
            itemId = itemId,
            order = order,
            quantity = checklistItemInput.quantity,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )
        val checklistItemDocRef = checklistItemsRef.add(checklistItem).await()

        return checklistItemDocRef.id.toLong()
    }

    suspend fun updateChecklistItem(checklistItemId: Long, checklistItemInput: ChecklistItemInput) {
        val currentDateTime = DateUtility.getCurrentDateTime()

        // Get the checklist item from Firestore
        val docRef = checklistItemsRef.document(checklistItemId.toString())
        val docSnapshot = docRef.get().await()
        if (!docSnapshot.exists()) return

        val checklistItem = docSnapshot.toObject(ChecklistItem::class.java) ?: return
        val updatedChecklistItem = checklistItem.copy(
            quantity = checklistItemInput.quantity,
            updatedAt = currentDateTime
        )

        // Update checklist item in Firestore
        docRef.set(updatedChecklistItem).await()

        // Update the corresponding item in Firestore
        val itemDocRef = itemsRef.document(checklistItem.itemId.toString())
        val itemSnapshot = itemDocRef.get().await()
        if (itemSnapshot.exists()) {
            val updatedItem = itemSnapshot.toObject(Item::class.java)?.copy(
                name = checklistItemInput.name,
                price = checklistItemInput.price,
                category = checklistItemInput.category,
                measureType = checklistItemInput.measureType,
                measureValue = checklistItemInput.measureValue,
                photoRef = checklistItemInput.photoRef,
                updatedAt = currentDateTime
            )
            updatedItem?.let { itemDocRef.set(it).await() }
        }
    }

    suspend fun changeChecklistOrder(checklistId: Long, checklistItemId: Long, newOrder: Int) {
        val checklistItems = checklistItemsRef
            .whereEqualTo("checklistId", checklistId)
            .orderBy("order")
            .get()
            .await()
            .toObjects(ChecklistItem::class.java)

        if (newOrder < 0 || newOrder >= checklistItems.size) {
            throw IllegalArgumentException("New order is out of bounds")
        }

        val checklistItemOrder = checklistItems.find { it.id == checklistItemId } ?: return
        val updatedList = checklistItems.toMutableList()
        updatedList.remove(checklistItemOrder)
        updatedList.add(newOrder - 1, checklistItemOrder)

        updatedList.forEachIndexed { index, checklistItem ->
            checklistItemsRef.document(checklistItem.id.toString())
                .update("order", index + 1)
                .await()
        }
    }

    suspend fun deleteChecklistItem(checklistItem: ChecklistItem) {
        checklistItemsRef.document(checklistItem.id.toString()).delete().await()
        reorderChecklistItems(checklistItem.checklistId)
    }

    suspend fun deleteChecklistItem(checklistId: Long) {
        val querySnapshot = checklistItemsRef.whereEqualTo("checklistId", checklistId).get().await()
        querySnapshot.documents.forEach { it.reference.delete().await() }
    }

    suspend fun deleteChecklistItemAndItem(checklistId: Long, itemId: Long) {
        checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .whereEqualTo("itemId", itemId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete().await() }

        itemsRef.document(itemId.toString()).delete().await()
    }

    suspend fun getChecklistItem(id: Long): ChecklistItemFull {
        val docSnapshot = checklistItemsRef.document(id.toString()).get().await()
        return docSnapshot.toObject(ChecklistItemFull::class.java)!!
    }

    fun getChecklistItems(checklistId: Long, orderBy: ChecklistItemOrder): Flow<List<ChecklistItemFull>> = flow {
        val query = checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .orderBy(orderBy.order)
            .get()
            .await()

        emit(query.toObjects(ChecklistItemFull::class.java))
    }

    fun searchChecklistItems(checklistId: Long, searchQuery: String): Flow<List<ChecklistItemFull>> = flow {
        val query = checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .whereGreaterThanOrEqualTo("name", searchQuery)
            .whereLessThanOrEqualTo("name", searchQuery + "\uf8ff")
            .get()
            .await()

        emit(query.toObjects(ChecklistItemFull::class.java))
    }

    suspend fun getTotalChecklistItems(checklistId: Long): Int {
        return checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .get()
            .await()
            .size()
    }

    suspend fun getTotalChecklistItemPrice(checklistId: Long): Double {
        val querySnapshot = checklistItemsRef.whereEqualTo("checklistId", checklistId).get().await()
        return querySnapshot.documents.sumOf { it.getDouble("price") ?: 0.0 }
    }

    private suspend fun getMaxOrder(checklistId: Long): Int {
        val querySnapshot = checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .orderBy("order", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.getLong("order")?.toInt() ?: 0
    }

    private suspend fun reorderChecklistItems(checklistId: Long) {
        val querySnapshot = checklistItemsRef.whereEqualTo("checklistId", checklistId)
            .orderBy("order")
            .get()
            .await()

        querySnapshot.documents.forEachIndexed { index, doc ->
            doc.reference.update("order", index + 1).await()
        }
    }

}