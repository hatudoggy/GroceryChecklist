package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
        return emptyFlow()
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
        return emptyFlow()
    }

    override fun getAllHistoryItemsOrderAndCheckedFilter(
        historyId: Long,
        order: ChecklistItemOrder,
        isChecked: Boolean
    ): Flow<List<HistoryItem>> {
        return emptyFlow()
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        return emptyFlow()
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        return emptyFlow()
    }

    override suspend fun aggregateTotalHistoryItems(historyId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double? {
        TODO("Not yet implemented")
    }

    override fun aggregateTotalPriceMonth(date: String): Flow<Double?> {
        return emptyFlow()
    }

    override fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>> {
        return emptyFlow()
    }


    override fun toFirestoreModel(obj: HistoryItem): Map<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): HistoryItem {
        TODO("Not yet implemented")
    }

}