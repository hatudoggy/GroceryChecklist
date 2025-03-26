package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class FHistoryItemDAOImpl: FBaseDAOImpl<HistoryItem>(
    FirestoreCollections.HISTORY_ITEMS
), HistoryItemDAO {

    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoryItemById(historyItemId: Long): HistoryItem {
        TODO("Not yet implemented")
    }

    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>> {
        TODO("Not yet implemented")
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
        TODO("Not yet implemented")
    }

    override fun getAllHistoryItemsOrderAndCheckedFilter(
        historyId: Long,
        order: ChecklistItemOrder,
        isChecked: Boolean
    ): Flow<List<HistoryItem>> {
        TODO("Not yet implemented")
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        TODO("Not yet implemented")
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun aggregateTotalHistoryItems(historyId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double? {
        TODO("Not yet implemented")
    }

    override fun aggregateTotalPriceMonth(date: String): Flow<Double?> {
        TODO("Not yet implemented")
    }

    override fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>> {
        TODO("Not yet implemented")
    }


    override fun toFirestoreModel(obj: HistoryItem): Map<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): HistoryItem {
        TODO("Not yet implemented")
    }

}