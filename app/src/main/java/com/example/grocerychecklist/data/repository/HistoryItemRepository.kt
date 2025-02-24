package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.viewmodel.checklist.ChecklistData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryItemRepository(
    private val historyItemDAO: HistoryItemDAO
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

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

    suspend fun addHistoryItems(historyId: Long, items: List<ChecklistData>): List<Long> {

        val currentDateTime = DateUtility.getCurrentDateTime()

        val historyItems = items.map { item ->
            HistoryItem(
                historyId = historyId,
                checklistItemId = item.checklistId,
                name = item.name,
                price = item.price,
                category = item.category.name,
                measureType = item.measurement.name,
                measureValue = item.measurementValue,
                photoRef = item.photoRef,
                order = item.order,
                quantity = item.quantity,
                isChecked = item.isChecked,
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

    fun getTotalItemPriceOnMonth(date: LocalDate): Flow<Double?> {
        val stringDate = date.format(formatter)
        return historyItemDAO.aggregateTotalPriceMonth(stringDate)
    }


    fun getCategoryDistributionOnMonth(date: LocalDate): Flow<List<HistoryItemAggregated>> {
        val stringDate = date.format(formatter)
        return historyItemDAO.aggregateCategoryBreakdownMonth(stringDate)
    }
}