package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dto.HistoryFirestore
import com.example.grocerychecklist.data.dto.HistoryItemAggregatedFirestore
import com.example.grocerychecklist.data.dto.HistoryItemFirestore
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.util.IdGenerator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Locale

class FHistoryItemDAOImpl: FBaseDAOImpl<HistoryItem>(
    FirestoreCollections.HISTORY_ITEMS
), HistoryItemDAO {

    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long> {
        if (historyItems.isEmpty()) return emptyList()

        val firestore = Firebase.firestore
        val batch = firestore.batch()

        val historyId = historyItems.first().historyId.toString()
        val historyRef = firestore
            .collection(FirestoreCollections.USERS)
            .document(currentUser.uid)
            .collection(FirestoreCollections.HISTORIES)
            .document(historyId)

        val generatedIds = historyItems.map { item ->
            val newItemId = IdGenerator.nextID()
            val itemRef = db.document(newItemId.toString())
            val firestoreModel = toFirestoreModel(item.copy(historyId = historyId.toLong()))
            batch.set(itemRef, firestoreModel)

            newItemId
        }

        val totalPrice = historyItems.sumOf { it.price * it.quantity }
        val totalItems = historyItems.sumOf { it.quantity }

        val aggregatedItems = historyItems
            .groupBy { it.category }
            .map { (category, items) ->
                mapOf(
                    "category" to category,
                    "sumOfPrice" to items.sumOf { it.price * it.quantity },
                    "totalItems" to items.sumOf { it.quantity }
                )
            }

        batch.update(historyRef, mapOf(
            "totalPrice" to totalPrice,
            "totalItems" to totalItems,
            "aggregatedItems" to aggregatedItems
        ))

        batch.commit().await()

        return generatedIds
    }

    override suspend fun getHistoryItemById(historyItemId: Long): HistoryItem {
        val snapshot = db.document(historyItemId.toString()).get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("History Item with id $historyItemId not found.")
        return fromFirestoreModel(snapshot, snapshot.id.toLong())
    }

    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>> {
        return db
            .whereEqualTo("historyId", historyId)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
        return db
            .whereEqualTo("historyId", historyId)
            .orderBy(order.order, Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun getAllHistoryItemsOrderAndCheckedFilter(
        historyId: Long,
        order: ChecklistItemOrder,
        isChecked: Boolean
    ): Flow<List<HistoryItem>> {
        return db
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("isChecked", isChecked)
            .orderBy(order.order, Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        return db
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("name", qName)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        return db
            .whereEqualTo("historyId", historyId)
            .whereEqualTo("category", category)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override suspend fun aggregateTotalHistoryItems(historyId: Long): Int {
        val historyRef = Firebase.firestore
            .collection(FirestoreCollections.USERS).document(currentUser.uid)
            .collection(FirestoreCollections.HISTORIES).document(historyId.toString())

        val snapshot = historyRef.get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("History with id $historyId not found.")
        val doc = snapshot.toObject(HistoryFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse history item data.")
        return doc.totalItems
            ?: throw IllegalStateException("Total Items doesn't have a value")
    }

    override suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double {
        val historyRef = Firebase.firestore
            .collection(FirestoreCollections.USERS).document(currentUser.uid)
            .collection(FirestoreCollections.HISTORIES).document(historyId.toString())

        val snapshot = historyRef.get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("History with id $historyId not found.")
        val doc = snapshot.toObject(HistoryFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse history item data.")
        return doc.totalPrice
            ?: throw IllegalStateException("Total Price doesn't have a value")
    }

    override fun aggregateTotalPriceMonth(date: String): Flow<Double?> {
        val historyRef = Firebase.firestore
            .collection(FirestoreCollections.USERS).document(currentUser.uid)
            .collection(FirestoreCollections.HISTORIES)

        return historyRef
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(date))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(date))
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.sumOf { document ->
                    document.getDouble("totalPrice") ?: 0.0
                }
            }
    }

    override fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>> {
        val historyRef = Firebase.firestore
            .collection(FirestoreCollections.USERS).document(currentUser.uid)
            .collection(FirestoreCollections.HISTORIES)

        return historyRef
            .whereGreaterThanOrEqualTo("createdAt", DateUtility.getStartOfMonthTimestamp(date))
            .whereLessThan("createdAt", DateUtility.getEndOfMonthTimestamp(date))
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents
                    .flatMap { documentSnapshot ->
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
                    .groupBy { it.category }
                    .map { (category, items) ->
                        val totalSum = items.sumOf { it.sumOfPrice }
                        val totalItemCount = items.sumOf { it.totalItems }
                        HistoryItemAggregated(totalSum, totalItemCount, category)
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