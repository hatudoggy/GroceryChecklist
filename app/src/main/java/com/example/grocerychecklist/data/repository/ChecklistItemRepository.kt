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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
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
        val itemId = itemDAO.insert(item)

        val order = checklistItemDAO.getChecklistItemMaxOrder(checklistId) + 1
        val checklistItem = ChecklistItem(
            checklistId = checklistId,
            itemId = itemId,
            order = order,
            quantity = checklistItemInput.quantity,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )

        val checklistItemId = checklistItemDAO.insert(checklistItem)

        val checklistItemDocRef = checklistItemsRef.add(checklistItem).await()

        return checklistItemDocRef.id.toLong()
    }

    suspend fun updateChecklistItem(checklistItemId: Long, checklistItemInput: ChecklistItemInput) {
        val checklistItem = checklistItemDAO.getChecklistItemById(checklistItemId)
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedItem = checklistItem.item.copy(
            name = checklistItemInput.name,
            price = checklistItemInput.price,
            category = checklistItemInput.category,
            measureType = checklistItemInput.measureType,
            measureValue = checklistItemInput.measureValue,
            photoRef = checklistItemInput.photoRef,
            updatedAt = currentDateTime
        )
        val updatedChecklistItem = checklistItem.checklistItem.copy(
            quantity = checklistItemInput.quantity,
            updatedAt = currentDateTime
        )

        itemDAO.update(updatedItem)
        checklistItemDAO.update(updatedChecklistItem)

    }

    suspend fun changeChecklistOrder(checklistId: Long, checklistItemId: Long, newOrder: Int) {
        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId).take(1).first()

        if (newOrder < 0 || newOrder >= checklistItems.size) {
            throw IllegalArgumentException("New order is out of bounds")
        }

        val checklistItemOrder = checklistItems.find { it.id == checklistItemId } ?: return
        val updatedList = checklistItems.toMutableList()
        updatedList.remove(checklistItemOrder)
        updatedList.add(newOrder - 1, checklistItemOrder)

        val adjustedItems = updatedList.mapIndexed { index, checklistItem ->
            checklistItem.copy(order = index + 1)
        }.toMutableList()

        adjustedItems.forEach { checklistItemDAO.update(it) }
    }

    suspend fun deleteChecklistItem(checklistItem: ChecklistItem) {
        checklistItemDAO.delete(checklistItem)

        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistItem.checklistId)
        checklistItems.take(1).first().forEachIndexed { index, item ->
            val updatedItem = item.copy(order = index + 1)
            checklistItemDAO.update(updatedItem)
        }


    }

    suspend fun deleteChecklistItem(checklistId: Long) {
        checklistItemDAO.deleteChecklistById(checklistId)

        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
        checklistItems.take(1).first().forEachIndexed { index, item ->
            val updatedItem = item.copy(order = index + 1)
            checklistItemDAO.update(updatedItem)
        }


    }

    suspend fun deleteChecklistItemAndItem(checklistId: Long, itemId: Long) {
        checklistItemDAO.deleteChecklistById(checklistId)
        itemDAO.deleteItemById(itemId)

        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
        checklistItems.take(1).first().forEachIndexed { index, item ->
            val updatedItem = item.copy(order = index + 1)
            checklistItemDAO.update(updatedItem)
        }


    }

    suspend fun getChecklistItem(id: Long): ChecklistItemFull {
        return checklistItemDAO.getChecklistItemById(id)
    }

    fun getChecklistItems(checklistId: Long, orderBy: ChecklistItemOrder): Flow<List<ChecklistItemFull>> {
        return when (orderBy) {
            ChecklistItemOrder.Order ->
                checklistItemDAO.getAllChecklistItemsOrderedByOrder(checklistId)

            ChecklistItemOrder.Name ->
                checklistItemDAO.getAllChecklistItemsOrderedByName(checklistId)

            ChecklistItemOrder.Price ->
                checklistItemDAO.getAllChecklistItemsOrderedByPrice(checklistId)

            ChecklistItemOrder.Date ->
                checklistItemDAO.getAllChecklistItemsOrderedByPrice(checklistId)

        }
    }

    fun searchChecklistItems(checklistId: Long, searchQuery: String): Flow<List<ChecklistItemFull>> {
        return checklistItemDAO.getAllChecklistItemsByName(checklistId,searchQuery)
    }

    suspend fun getTotalChecklistItems(checklistId: Long): Int {
        return checklistItemDAO.aggregateTotalChecklistItems(checklistId)
    }

    suspend fun getTotalChecklistItemPrice(checklistId: Long): Double {
        return checklistItemDAO.aggregateTotalChecklistItemPrice(checklistId) ?: 0.00
    }

}