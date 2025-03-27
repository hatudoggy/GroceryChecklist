package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dto.HistoryFirestore
import com.example.grocerychecklist.data.dto.HistoryItemAggregatedFirestore
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.util.IdGenerator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class FHistoryDAOImpl: FBaseDAOImpl<History>(
    FirestoreCollections.HISTORIES
), HistoryDAO {

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
                    val totalPrice = doc.getDouble("totalPrice") ?: 0.0
                    HistoryPriced(history, totalPrice)
                }
            }
    }

    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>> {
        return db
            .document(historyId.toString())
            .snapshots()
            .map { documentSnapshot ->
                val aggregatedItemsList = documentSnapshot.get("aggregatedItems")
                        as? List<*> ?: emptyList<HistoryItemAggregatedFirestore>()

                aggregatedItemsList.mapNotNull { item ->
                    try {
                        val firestoreItem = Gson().fromJson(Gson().toJson(item),
                            HistoryItemAggregatedFirestore::class.java)
                        firestoreItem.toDomainModel()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        return db
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        val historyFirestore = document.toObject(HistoryFirestore::class.java)
                            ?.apply { id = document.id.toLong() }

                        val totalPrice = historyFirestore?.totalPrice ?: 0.0
                        val aggregatedItemsList = historyFirestore?.aggregatedItems ?: emptyList()

                        val aggregatedItems = aggregatedItemsList.mapNotNull { item ->
                            try {
                                val firestoreItem = Gson().fromJson(Gson().toJson(item),
                                    HistoryItemAggregatedFirestore::class.java)
                                firestoreItem.toDomainModel()
                            } catch (e: Exception) {
                                null
                            }
                        }.take(3)

                        historyFirestore?.let {
                            HistoryMapped(
                                history = it.toHistory(historyFirestore.id),
                                totalPrice = totalPrice,
                                aggregatedItems = aggregatedItems
                            )
                        }
                    } catch (e: Exception) {
                        null
                    }
                }.let { if (limit != null) it.take(limit) else it }
            }
    }

    override fun getHistoriesFromDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<History>> {
        return db
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThan("createdAt", endDate)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
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