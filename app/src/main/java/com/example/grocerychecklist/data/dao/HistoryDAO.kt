package com.example.grocerychecklist.data.dao

import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface HistoryDAO {

    suspend fun insert(history: History): Long

    suspend fun getHistoryById(historyId: Long): History

    fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>>

    fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>>

    fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>>

    suspend fun getHistoryWithAggregatedItems(limit: Int? = null): Flow<List<HistoryMapped>>

    fun getHistoriesFromDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<History>>

}