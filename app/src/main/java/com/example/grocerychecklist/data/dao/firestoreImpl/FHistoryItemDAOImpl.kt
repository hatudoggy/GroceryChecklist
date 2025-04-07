package com.example.grocerychecklist.data.dao.firestoreImpl

import android.util.Log
import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dto.HistoryItemAggregatedFirestore
import com.example.grocerychecklist.data.dto.HistoryItemFirestore
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.util.IdGenerator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Month
import java.util.Locale

class FHistoryItemDAOImpl: FBaseDAOImpl<HistoryItem>(
    FirestoreCollections.HISTORY_ITEMS
), HistoryItemDAO {

    private val TAG = "FHistoryItemDAOImpl"

    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long> {
        Log.d(TAG, "insertBatch() called with ${historyItems.size} items")
        if (historyItems.isEmpty()) {
            Log.d(TAG, "insertBatch() - empty list, returning empty result")
            return emptyList()
        }

        val historyId = historyItems.first().historyId.toString()
        Log.d(TAG, "insertBatch() - processing historyId: $historyId")

        val generatedIds = historyItems.map { item ->
            val newItemId = IdGenerator.nextID()
            val itemRef = db.document(newItemId.toString())
            val firestoreModel = toFirestoreModel(item.copy(historyId = historyId.toLong()))
            itemRef.set(firestoreModel).await()
            Log.d(TAG, "insertBatch() - inserted item with id: $newItemId")
            newItemId
        }

        Log.d(TAG, "insertBatch() completed, generated IDs: $generatedIds")
        return generatedIds
    }

    override fun getHistoryItemById(historyItemId: Long): Flow<HistoryItem> {
        Log.d(TAG, "getHistoryItemById() called for id: $historyItemId")
        return flow {
            val documentReference = db.document("$historyItemId")
            val snapshot = documentReference.get().await()
            if (!snapshot.exists()) {
                Log.e(TAG, "getHistoryItemById() - item not found for id: $historyItemId")
                throw NoSuchElementException("History Item with id $historyItemId not found.")
            }
            val result = fromFirestoreModel(snapshot, historyItemId)
            Log.d(TAG, "getHistoryItemById() - retrieved item: $result")
            emit(result)
        }
    }

    private fun getHistoryItemsFlow(
        queryModifier: (Query) -> Query
    ): Flow<List<HistoryItem>> {
        Log.d(TAG, "getHistoryItemsFlow() called")
        return queryModifier(db)
            .snapshots()
            .map { querySnapshot ->
                val result = querySnapshot.toHistoryItemList()
                Log.d(TAG, "getHistoryItemsFlow() - retrieved ${result.size} items")
                result
            }
    }

    private fun QuerySnapshot.toHistoryItemList(): List<HistoryItem> {
        Log.d(TAG, "toHistoryItemList() called with ${this.documents.size} documents")
        return this.documents.map {
            val result = fromFirestoreModel(it, it.id.toLong())
            Log.d(TAG, "toHistoryItemList() - converted document to: $result")
            result
        }
    }

    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>> {
        Log.d(TAG, "getAllHistoryItems() called for historyId: $historyId")
        return getHistoryItemsFlow { query ->
            query.whereEqualTo("historyId", historyId)
        }
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
        Log.d(TAG, "getAllHistoryItemsOrderFilter() called for historyId: $historyId with order: $order")
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .orderBy(order.order, Query.Direction.DESCENDING)
        }
    }

    override fun getAllHistoryItemsOrderAndCheckedFilter(
        historyId: Long,
        order: ChecklistItemOrder,
        isChecked: Boolean
    ): Flow<List<HistoryItem>> {
        Log.d(TAG, "getAllHistoryItemsOrderAndCheckedFilter() called for historyId: $historyId with order: $order and isChecked: $isChecked")
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("isChecked", isChecked)
            .orderBy(order.order, Query.Direction.DESCENDING)
        }
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        Log.d(TAG, "getAllHistoryItemsByName() called for historyId: $historyId with name: $qName")
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("name", qName)
        }
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        Log.d(TAG, "getAllHistoryItemsByCategory() called for historyId: $historyId with category: $category")
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("category", category)
        }
    }

    override fun aggregateTotalHistoryItems(historyId: Long): Flow<Int> {
        Log.d(TAG, "aggregateTotalHistoryItems() called for historyId: $historyId")
        return flow {
            val result = getHistoryDocument(historyId).size()
            Log.d(TAG, "aggregateTotalHistoryItems() - count: $result")
            emit(result)
        }
    }

    override fun aggregateTotalHistoryItemPrice(historyId: Long): Flow<Double> {
        Log.d(TAG, "aggregateTotalHistoryItemPrice() called for historyId: $historyId")
        return flow {
            val documents = getHistoryDocument(historyId).documents
            val sum = documents.sumOf { document ->
                document.getDouble("price") ?: 0.0
            }
            Log.d(TAG, "aggregateTotalHistoryItemPrice() - sum: $sum from ${documents.size} documents")
            emit(sum)
        }
    }

    private suspend fun getHistoryDocument(historyId: Long): QuerySnapshot {
        Log.d(TAG, "getHistoryDocument() called for historyId: $historyId")
        val result = db.whereEqualTo("historyId", historyId).get().await()
        Log.d(TAG, "getHistoryDocument() - retrieved ${result.size()} documents")
        return result
    }

    override fun aggregateTotalPriceMonth(month: Month): Flow<Double?> {
        Log.d(TAG, "aggregateTotalPriceMonth() called for month: $month")
        return db
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(month))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(month))
            .snapshots()
            .map { querySnapshot ->
                val sum = querySnapshot.documents.sumOf { document ->
                    document.getDouble("price") ?: 0.0
                }
                Log.d(TAG, "aggregateTotalPriceMonth() - sum for $month: $sum")
                sum
            }
    }

    override fun aggregateCategoryBreakdownMonth(month: Month): Flow<List<HistoryItemAggregated>> {
        Log.d(TAG, "aggregateCategoryBreakdownMonth() called for month: $month")
        val historyItems = getHistoryItemsFlow { query -> query
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(month))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(month))
        }

        return historyItems.map {
            val result = it.groupBy { item -> item.category }
                .map { (category, items) ->
                    HistoryItemAggregated(
                        items.sumOf { item -> item.price },
                        items.size,
                        category
                    )
                }
            Log.d(TAG, "aggregateCategoryBreakdownMonth() - result: $result")
            result
        }
    }

    override fun toFirestoreModel(obj: HistoryItem): Map<String, Any?> {
        val firestoreModel = HistoryItemFirestore.fromHistoryItem(obj).toMap()
        val result = firestoreModel - "id"
        Log.d(TAG, "toFirestoreModel() - converted $obj to $result")
        return result
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): HistoryItem {
        val doc = snapshot.toObject(HistoryItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse history item data.")
        val result = doc.toHistoryItem(id)
        Log.d(TAG, "fromFirestoreModel() - converted document to $result")
        return result
    }

    override fun getId(obj: HistoryItem): Long {
        val result = obj.id
        Log.d(TAG, "getId() - returning id: $result")
        return result
    }
}