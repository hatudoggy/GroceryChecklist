package com.example.grocerychecklist.data.dao.firestoreImpl

import android.util.Log
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dto.HistoryFirestore
import com.example.grocerychecklist.data.dto.HistoryItemAggregatedFirestore
import com.example.grocerychecklist.data.dto.toTimestamp
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.util.IdGenerator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset

class FHistoryDAOImpl: FBaseDAOImpl<History>(
    FirestoreCollections.HISTORIES
), HistoryDAO {

    private val fHistoryItemDAOImpl: FHistoryItemDAOImpl by lazy {
        FHistoryItemDAOImpl()
    }

    override suspend fun insert(history: History): Long {
        val newId = IdGenerator.nextID()
        val firestoreModel = toFirestoreModel(history)
        db.document(newId.toString()).set(firestoreModel).await()
        return newId
    }

    override suspend fun getHistoryById(historyId: Long): History {
        val snapshot = db.document(historyId.toString()).get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("History with id $historyId not found.")
        return fromFirestoreModel(snapshot, snapshot.id.toLong())
    }

    override fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>> {
        return db
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>> {
        return db
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    val history = fromFirestoreModel(doc, doc.id.toLong())
                    val totalPrice = fHistoryItemDAOImpl.aggregateTotalHistoryItemPrice(history.id)
                    HistoryPriced(history, totalPrice)
                }
            }
    }

    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>> {
        val historyItems = fHistoryItemDAOImpl.getAllHistoryItems(historyId)
        return historyItems.map {
            it.groupBy { item -> item.category }
                .map { (category, items) ->
                    HistoryItemAggregated(
                        items.sumOf { item -> item.price },
                        items.sumOf { item -> item.quantity },
                        category
                    )
                }
        }
    }

    override suspend fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        val histories = getAllHistoriesOrderedLimitWithSum(limit ?: Int.MAX_VALUE)

        return histories.map { historiesPriced ->
            historiesPriced.map { historyPriced ->
                val history = historyPriced.history
                val aggregatedItems = getHistoryItemAggregated(history.id).first()
                HistoryMapped(history, historyPriced.totalPrice, aggregatedItems)
            }
        }
    }

    override fun getHistoriesFromDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<History>> {
        val startTimestamp = startDate.toTimestamp()
        val endTimestamp = endDate.toTimestamp()

        return db
            .whereGreaterThanOrEqualTo("createdAt", startTimestamp)
            .whereLessThan("createdAt", endTimestamp)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    val id = document.id.toLongOrNull()
                    if (id == null) {
                        Log.d("FHistoryDAOImpl", "Warning: Document ID is not a valid Long: ${document.id}")
                        return@mapNotNull null
                    }
                    try {
                        fromFirestoreModel(document, id)
                    } catch (e: Exception) {
                        Log.e("FHistoryDAOImpl", "Error converting document to History: ${document.id}, Error: ${e.message}")
                        null
                    }
                }
            }
    }

    override fun toFirestoreModel(obj: History): Map<String, Any?> {
        val firestoreModel = HistoryFirestore.fromHistory(obj).toMap()
        return firestoreModel - "id"
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): History {
        val doc = snapshot.toObject(HistoryFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse history data.")
        return doc.toHistory(id)
    }

    override fun getId(obj: History): Long {
        return obj.id
    }

}