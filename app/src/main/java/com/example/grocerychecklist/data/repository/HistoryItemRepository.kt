package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow

class HistoryItemRepository(
    private val historyItemDAO: HistoryItemDAO
) {

    suspend fun addHistoryItems(
        historyId: Long, checklistItems: List<ChecklistItemFull>, checkedItems: Set<Long>): List<Long> {

        val currentDateTime = DateUtility.getCurrentDateTime()

        val historyItems = checklistItems.map { item ->
            val isChecked = checkedItems.contains(item.checklistItem.id)
            HistoryItem(
                historyId = historyId,
                checklistItemId = item.checklistItem.checklistId,
                name = item.item.name,
                price = item.item.price,
                category = item.item.category,
                measureType = item.item.measureType,
                measureValue = item.item.measureValue,
                photoRef = item.item.photoRef,
                order = item.checklistItem.order,
                quantity = item.checklistItem.quantity,
                isChecked = isChecked,
                createdAt = currentDateTime
            )
        }

        return historyItemDAO.insertBatch(historyItems)
    }

    suspend fun getHistoryItem(id: Long): HistoryItem {
        return historyItemDAO.getHistoryItemById(id)
    }

    fun getHistoryItems(historyId: Long, groupBy: ChecklistItemOrder): Flow<List<HistoryItem>> {
        return historyItemDAO.getAllHistoryItemsOrderFilter(historyId, groupBy)
    }

    fun getHistoryItems(historyId: Long, groupBy: ChecklistItemOrder, isChecked: Boolean): Flow<List<HistoryItem>> {
        return historyItemDAO.getAllHistoryItemsOrderAndCheckedFilter(historyId, groupBy, isChecked)
    }

    suspend fun getTotalHistoryItems(historyId: Long): Int {
        return historyItemDAO.aggregateTotalHistoryItems(historyId)
    }

    suspend fun getTotalHistoryItemPrice(historyId: Long): Double {
        return historyItemDAO.aggregateTotalHistoryItemPrice(historyId) ?: 0.00
    }
}