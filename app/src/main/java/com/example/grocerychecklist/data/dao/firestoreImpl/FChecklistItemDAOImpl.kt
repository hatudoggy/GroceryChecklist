package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dto.ChecklistItemFirestore
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface IFChecklistItemDAOImpl : ChecklistItemDAO {
    suspend fun deleteChecklistByItemId(itemId: Long): Int
}

@OptIn(ExperimentalCoroutinesApi::class)
class FChecklistItemDAOImpl : FBaseIUDDAOImpl<ChecklistItem>(
    FirestoreCollections.CHECKLIST_ITEMS
), IFChecklistItemDAOImpl {

    private val fItemDAOImpl: FItemDAOImpl by lazy {
        FItemDAOImpl()
    }

    override suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull {
        val query = db.whereEqualTo("id", checklistItemId).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Checklist with id $checklistItemId not found.")

        val checklistItem = fromFirestoreModel(snapshot)

        val item = fItemDAOImpl.getItemById(checklistItem.itemId)

        return ChecklistItemFull(checklistItem, item)
    }

    override fun getAllChecklistItems(checklistItemId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistItemId).snapshots().map { querySnapshot ->
            querySnapshot.documents.map {
                val checklistItem = fromFirestoreModel(it)
                val item = fItemDAOImpl.getItemById(checklistItem.itemId)

                ChecklistItemFull(checklistItem, item)
            }
        }
    }

    override fun getAllChecklistItemsOrderedByOrder(checklistItemId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistItemId)
            .orderBy("order", Query.Direction.DESCENDING).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map {
                    val checklistItem = fromFirestoreModel(it)
                    val item = fItemDAOImpl.getItemById(checklistItem.itemId)

                    ChecklistItemFull(checklistItem, item)
                }
            }
    }

    override fun getAllChecklistItemsBaseOrderedByOrder(checklistItemId: Long): Flow<List<ChecklistItem>> {
        return db.whereEqualTo("checklistId", checklistItemId)
            .orderBy("order", Query.Direction.DESCENDING).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map {
                    fromFirestoreModel(it)
                }
            }
    }

    override fun getAllChecklistItemsOrderedByName(checklistItemId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistItemId).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it) }
            }
            .flatMapMerge { checklistItems ->
                flow {
                    val items = checklistItems.map { checklistItem ->
                        val item = fItemDAOImpl.getItemById(checklistItem.itemId)
                        ChecklistItemFull(checklistItem, item)
                    }
                    emit(items.sortedBy { it.item.name })
                }
            }
    }

    override fun getAllChecklistItemsOrderedByPrice(checklistItemId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistItemId).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it) }
            }
            .flatMapMerge { checklistItems ->
                flow {
                    val items = checklistItems.map { checklistItem ->
                        val item = fItemDAOImpl.getItemById(checklistItem.itemId)
                        ChecklistItemFull(checklistItem, item)
                    }
                    emit(items.sortedBy { it.item.price })
                }
            }
    }

    override fun getAllChecklistItemsByName(
        checklistItemId: Long,
        qName: String
    ): Flow<List<ChecklistItemFull>> {
        return flow {
            val item = fItemDAOImpl.getItemByName(qName)

            val checklistItemsQuery = db
                .whereEqualTo("checklistId", checklistItemId)
                .whereEqualTo("itemId", item.id)
                .get()
                .await()

            val checklistItems = checklistItemsQuery.documents.map { fromFirestoreModel(it) }

            val checklistItemFullList = checklistItems.map { checklistItem ->
                ChecklistItemFull(checklistItem, item)
            }

            emit(checklistItemFullList)
        }
    }

    override fun getAllChecklistItemsByCategory(
        checklistItemId: Long,
        category: String
    ): Flow<List<ChecklistItemFull>> {
        return flow {
            val item = fItemDAOImpl.getItemByCategory(category)

            val checklistItemsQuery = db
                .whereEqualTo("checklistId", checklistItemId)
                .whereEqualTo("itemId", item.id)
                .get()
                .await()

            val checklistItems = checklistItemsQuery.documents.map { fromFirestoreModel(it) }

            // Combine checklist items with the found item
            val checklistItemFullList = checklistItems.map { checklistItem ->
                ChecklistItemFull(checklistItem, item)
            }

            emit(checklistItemFullList)
        }
    }

    override suspend fun getChecklistItemMaxOrder(checklistItemId: Long): Int {
        val query = db.whereEqualTo("checklistId", checklistItemId)
            .orderBy("order", Query.Direction.DESCENDING).limit(1).get().await()

        return query.documents.firstOrNull()?.getLong("order")?.toInt() ?: 0
    }

    override suspend fun deleteChecklistById(checklistItemId: Long): Int {
        val querySnapshot = db.whereEqualTo("id", checklistItemId).get().await()

        if (querySnapshot.isEmpty) {
            throw NoSuchElementException("Checklist with id $checklistItemId not found.")
        }

        db.document(querySnapshot.documents.first().id).delete()

        return querySnapshot.size()
    }

    override suspend fun deleteChecklistByItemId(itemId: Long): Int {
        val querySnapshot = db.whereEqualTo("itemId", itemId).get().await()

        if (querySnapshot.isEmpty) {
            return 0 // Prevent the app from crashing OML
        }

        db.document(querySnapshot.documents.first().id).delete()

        return querySnapshot.size()
    }

    override suspend fun aggregateTotalChecklistItems(checklistItemId: Long): Int {
        val querySnapshot = db.whereEqualTo("checklistId", checklistItemId).get().await()

        return querySnapshot.size()
    }

    override suspend fun aggregateTotalChecklistItemPrice(checklistItemId: Long): Double? {
        val querySnapshot = db.whereEqualTo("checklistId", checklistItemId).get().await()

        return querySnapshot.documents.sumOf {
            val checklistItem = fromFirestoreModel(it)
            val item = fItemDAOImpl.getItemById(checklistItem.itemId)

            item.price
        }

    }

    override fun toFirestoreModel(obj: ChecklistItem): Map<String, Any?> {
        return ChecklistItemFirestore.fromChecklistItem(obj).toMap()
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): ChecklistItem {
        val doc = snapshot.toObject(ChecklistItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse checklist item data")

        return doc.toChecklistItem()
    }

}