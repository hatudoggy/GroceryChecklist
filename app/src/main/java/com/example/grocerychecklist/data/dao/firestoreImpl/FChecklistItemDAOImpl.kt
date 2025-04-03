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

@OptIn(ExperimentalCoroutinesApi::class)
class FChecklistItemDAOImpl : FBaseIUDDAOImpl<ChecklistItem>(
    FirestoreCollections.CHECKLIST_ITEMS
), ChecklistItemDAO {

    private val fItemDAOImpl: FItemDAOImpl by lazy {
        FItemDAOImpl()
    }

    override fun getChecklistItemById(checklistItemId: Long): Flow<ChecklistItemFull> {
        return flow {
            val snapshot = db.document(checklistItemId.toString()).get().await()
            if (!snapshot.exists())
                throw NoSuchElementException("Checklist Item with id $checklistItemId not found.")

            val checklistItem = fromFirestoreModel(snapshot, checklistItemId)

            val item = fItemDAOImpl.getItemById(checklistItem.itemId)

            emit(ChecklistItemFull(checklistItem, item))
        }
    }

    override fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistId).snapshots().map { querySnapshot ->
            querySnapshot.documents.map {
                val checklistItem = fromFirestoreModel(it, it.id.toLong())
                val item = fItemDAOImpl.getItemById(checklistItem.itemId)

                ChecklistItemFull(checklistItem, item)
            }
        }
    }

    override fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistId)
            .orderBy("order", Query.Direction.DESCENDING).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map {
                    val checklistItem = fromFirestoreModel(it, it.id.toLong())
                    val item = fItemDAOImpl.getItemById(checklistItem.itemId)

                    ChecklistItemFull(checklistItem, item)
                }
            }
    }

    override fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>> {
        return db.whereEqualTo("checklistId", checklistId)
            .orderBy("order", Query.Direction.DESCENDING).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map {
                    fromFirestoreModel(it, it.id.toLong())
                }
            }
    }

    override fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistId).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
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

    override fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return db.whereEqualTo("checklistId", checklistId).snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
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
        checklistId: Long,
        qName: String
    ): Flow<List<ChecklistItemFull>> {
        return flow {
            val item = fItemDAOImpl.getItemByName(qName)

            val checklistItemsQuery = db
                .whereEqualTo("checklistId", checklistId)
                .whereEqualTo("itemId", item.id)
                .get()
                .await()

            val checklistItems = checklistItemsQuery
                .documents.map { fromFirestoreModel(it, it.id.toLong()) }

            val checklistItemFullList = checklistItems.map { checklistItem ->
                ChecklistItemFull(checklistItem, item)
            }

            emit(checklistItemFullList)
        }
    }

    override fun getAllChecklistItemsByCategory(
        checklistId: Long,
        category: String
    ): Flow<List<ChecklistItemFull>> {
        return flow {
            val item = fItemDAOImpl.getItemByCategory(category)

            val checklistItemsQuery = db
                .whereEqualTo("checklistId", checklistId)
                .whereEqualTo("itemId", item.id)
                .get()
                .await()

            val checklistItems = checklistItemsQuery
                .documents.map { fromFirestoreModel(it, it.id.toLong()) }

            // Combine checklist items with the found item
            val checklistItemFullList = checklistItems.map { checklistItem ->
                ChecklistItemFull(checklistItem, item)
            }

            emit(checklistItemFullList)
        }
    }

    override fun getChecklistItemMaxOrder(checklistId: Long): Flow<Int> {
        return flow {
            val query = db.whereEqualTo("checklistId", checklistId)
                .orderBy("order", Query.Direction.DESCENDING).limit(1).get().await()

            emit(query.documents.firstOrNull()?.getLong("order")?.toInt() ?: 0)
        }
    }

    override suspend fun deleteChecklistById(checklistId: Long): Int {
        try {
            db.document(checklistId.toString()).delete()
            return 1
        } catch (e: Exception) {
            return 0
        }
    }

    override suspend fun deleteChecklistByItemId(itemId: Long): Int {
        val querySnapshot = db.whereEqualTo("itemId", itemId).get().await()

        if (querySnapshot.isEmpty) {
            throw NoSuchElementException("Checklist with id $itemId not found.")
        }

        db.document(querySnapshot.documents.first().id).delete()

        return querySnapshot.size()
    }

    override fun aggregateTotalChecklistItems(checklistId: Long): Flow<Int> {
        return flow {
            val querySnapshot = db.whereEqualTo("checklistId", checklistId).get().await()

            emit(querySnapshot.size())
        }
    }

    override fun aggregateTotalChecklistItemPrice(checklistId: Long): Flow<Double> {
        return flow {
            val querySnapshot = db.whereEqualTo("checklistId", checklistId).get().await()

            emit(
                querySnapshot.documents.sumOf {
                    val checklistItem = fromFirestoreModel(it, it.id.toLong())
                    val item = fItemDAOImpl.getItemById(checklistItem.itemId)

                    item.price
                }
            )
        }
    }

    override fun toFirestoreModel(obj: ChecklistItem): Map<String, Any?> {
        val firestoreModel = ChecklistItemFirestore.fromChecklistItem(obj).toMap()
        return firestoreModel - "id"
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): ChecklistItem {
        val doc = snapshot.toObject(ChecklistItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse checklist item data")
        return doc.toChecklistItem(id)
    }

    override fun getId(obj: ChecklistItem): Long {
        return obj.id
    }

}