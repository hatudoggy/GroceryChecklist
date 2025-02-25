package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

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

        return historyDAO.insert(history)
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

    suspend fun getAggregatedHistory(limit: Int? = null): Flow<List<HistoryMapped>> {
        return historyDAO.getHistoryWithAggregatedItems(limit)
    }

}