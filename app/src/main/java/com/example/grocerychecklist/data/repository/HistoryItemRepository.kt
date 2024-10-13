package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.HistoryItem
import kotlinx.coroutines.flow.Flow

class HistoryItemRepository(
    private val historyItemDAO: HistoryItemDAO
) {

    suspend fun addHistoryItems(checklistItems: List<HistoryItem>) {
        historyItemDAO.insertBatch(checklistItems)
    }

    suspend fun getHistoryItem(id: Int): HistoryItem {
        return historyItemDAO.getHistoryItemById(id)
    }

    fun getHistoryItems(historyId: Int, groupBy: ChecklistItemOrder): Flow<List<HistoryItem>> {
        return when (groupBy) {
            ChecklistItemOrder.Order ->
                historyItemDAO.getAllHistoryItemsOrderedByOrder(historyId)

            ChecklistItemOrder.Name ->
                historyItemDAO.getAllHistoryItemsOrderedByName(historyId)

            ChecklistItemOrder.Price ->
                historyItemDAO.getAllHistoryItemsOrderedByPrice(historyId)

        }
    }

    suspend fun getTotalHistoryItems(historyId: Int): Int {
        return historyItemDAO.aggregateTotalHistoryItems(historyId)
    }

    suspend fun getTotalHistoryItemPrice(historyId: Int): Double {
        return historyItemDAO.aggregateTotalHistoryItemPrice(historyId) ?: 0.00
    }
}