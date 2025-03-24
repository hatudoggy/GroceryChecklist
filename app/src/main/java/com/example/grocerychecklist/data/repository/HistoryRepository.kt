package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

class HistoryRepository(
    private val historyDAO: HistoryDAO
) {

    suspend fun addHistory(checklist: Checklist): Long {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val history = History(
            checklistId = checklist.id,
            name = checklist.name,
            description = checklist.description,
            icon = checklist.icon,
            iconColor = checklist.iconBackgroundColor,
            createdAt = currentDateTime
        )

        val historyId = historyDAO.insert(history)



        return historyId
    }

    suspend fun getHistory(id: Long): History {
        return historyDAO.getHistoryById(id)
    }

    fun getHistories(): Flow<List<History>> {
        return historyDAO.getAllHistoriesOrderedByCreatedAt()
    }

    fun getHistories(limit: Int): Flow<List<HistoryPriced>> {
        return historyDAO.getAllHistoriesOrderedLimitWithSum(limit)
    }

    fun getHistoriesFromDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<History>> {
        return historyDAO.getHistoriesFromDateRange(startDate, endDate)
    }

    suspend fun getAggregatedHistory(limit: Int? = null): Flow<List<HistoryMapped>> {
        return historyDAO.getHistoryWithAggregatedItems(limit)
    }

    fun getHistoriesFromMonth(month: Month): Flow<List<History>> {
        val startDate = LocalDate.of(LocalDate.now().year, month, 1).atStartOfDay()
        val endDate = LocalDate.of(LocalDate.now().year, month, month.length(
            LocalDate.now().isLeapYear
        )).atStartOfDay().plusDays(1).minusNanos(1)
        return historyDAO.getHistoriesFromDateRange(startDate, endDate)
    }
}