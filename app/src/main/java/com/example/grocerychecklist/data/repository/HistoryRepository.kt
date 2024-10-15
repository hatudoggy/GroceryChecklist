package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow

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
            iconColor = checklist.iconColor,
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

}