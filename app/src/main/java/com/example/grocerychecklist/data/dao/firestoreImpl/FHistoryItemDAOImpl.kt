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
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Month
import java.util.Locale

class FHistoryItemDAOImpl: FBaseDAOImpl<HistoryItem>(
    FirestoreCollections.HISTORY_ITEMS
), HistoryItemDAO {

    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long> {
        if (historyItems.isEmpty()) return emptyList()

        val historyId = historyItems.first().historyId.toString()

        val generatedIds = historyItems.map { item ->
            val newItemId = IdGenerator.nextID()
            val itemRef = db.document(newItemId.toString())
            val firestoreModel = toFirestoreModel(item.copy(historyId = historyId.toLong()))
            itemRef.set(firestoreModel).await()
            newItemId
        }

        return generatedIds
    }

    override fun getHistoryItemById(historyItemId: Long): Flow<HistoryItem> {
        return flow {
            val documentReference = db.document("$historyItemId")
            val snapshot = documentReference.get().await()
            if (!snapshot.exists())
                throw NoSuchElementException("History Item with id $historyItemId not found.")
            emit(fromFirestoreModel(snapshot, historyItemId))
        }
    }

    private fun getHistoryItemsFlow(
        queryModifier: (Query) -> Query
    ): Flow<List<HistoryItem>> {
        return queryModifier(db)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toHistoryItemList()
            }
    }

    private fun QuerySnapshot.toHistoryItemList(): List<HistoryItem> {
        return this.documents.map { fromFirestoreModel(it, it.id.toLong()) }
    }

    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>> {
        return getHistoryItemsFlow { query ->
            query.whereEqualTo("historyId", historyId)
        }
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
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
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("isChecked", isChecked)
            .orderBy(order.order, Query.Direction.DESCENDING)
        }
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("name", qName)
        }
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        return getHistoryItemsFlow { query -> query
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("category", category)
        }
    }

    override fun aggregateTotalHistoryItems(historyId: Long): Flow<Int> {
        return flow { emit(getHistoryDocument(historyId).size()) }
    }

    override fun aggregateTotalHistoryItemPrice(historyId: Long): Flow<Double> {
        return flow { emit(getHistoryDocument(historyId).documents.sumOf { document ->
            document.getDouble("price") ?: 0.0
        }) }
    }

    // Helper function to reduce code duplication
    private suspend fun getHistoryDocument(historyId: Long): QuerySnapshot {
        return db.whereEqualTo("historyId", historyId).get().await()
    }

    override fun aggregateTotalPriceMonth(month: Month): Flow<Double?> {
        return db
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(month))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(month))
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.sumOf { document ->
                    document.getDouble("price") ?: 0.0
                }
            }
    }

    override fun aggregateCategoryBreakdownMonth(month: Month): Flow<List<HistoryItemAggregated>> {
        val historyItems = getHistoryItemsFlow { query -> query
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(month))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(month))
        }

        return historyItems.map {
            it.groupBy { item -> item.category }
                .map { (category, items) ->
                    HistoryItemAggregated(
                        items.sumOf { item -> item.price },
                        items.size,
                        category
                    )
                }
        }
    }


    override fun toFirestoreModel(obj: HistoryItem): Map<String, Any?> {
        val firestoreModel = HistoryItemFirestore.fromHistoryItem(obj).toMap()
        return firestoreModel - "id"
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): HistoryItem {
        val doc = snapshot.toObject(HistoryItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse history item data.")
        return doc.toHistoryItem(id)
    }

    override fun getId(obj: HistoryItem): Long {
        return obj.id
    }
}