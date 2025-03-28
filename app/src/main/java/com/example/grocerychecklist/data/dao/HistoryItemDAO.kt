package com.example.grocerychecklist.data.dao

import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import kotlinx.coroutines.flow.Flow
import java.util.Locale.Category

interface HistoryItemDAO {

    suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long>

    suspend fun getHistoryItemById(historyItemId: Long): HistoryItem

    fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>>

    fun getAllHistoryItemsOrderFilter(historyId: Long, order: ChecklistItemOrder): Flow<List<HistoryItem>>

    fun getAllHistoryItemsOrderAndCheckedFilter(historyId: Long, order: ChecklistItemOrder, isChecked: Boolean): Flow<List<HistoryItem>>

    fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>>

    fun getAllHistoryItemsByCategory(historyId: Long, category: Category): Flow<List<HistoryItem>>

    suspend fun aggregateTotalHistoryItems(historyId: Long): Int

    suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double?

    fun aggregateTotalPriceMonth(date: String): Flow<Double?>

    fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>>

}