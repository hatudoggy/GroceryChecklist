package com.example.grocerychecklist.data.dao.manager

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dao.roomImpl.HistoryDAOImpl
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class HistoryDAOManager(
    private val roomDAO: HistoryDAOImpl,
    private val firestoreDAO: HistoryDAO
): HistoryDAO {

    private val useFirestore: Boolean
        get() = Firebase.auth.currentUser?.let { !it.isAnonymous } == true


    override suspend fun insert(history: History): Long {
        return if (useFirestore) {
            firestoreDAO.insert(history)
        } else {
            roomDAO.insert(history)
        }
    }

    override suspend fun getHistoryById(historyId: Long): History {
        return if (useFirestore) {
            firestoreDAO.getHistoryById(historyId)
        } else {
            roomDAO.getHistoryById(historyId)
        }
    }

    override fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoriesOrderedByCreatedAt()
        } else {
            roomDAO.getAllHistoriesOrderedByCreatedAt()
        }
    }

    override fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoriesOrderedLimitWithSum(limit)
        } else {
            roomDAO.getAllHistoriesOrderedLimitWithSum(limit)
        }
    }

    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>> {
        return if (useFirestore) {
            firestoreDAO.getHistoryItemAggregated(historyId)
        } else {
            roomDAO.getHistoryItemAggregated(historyId)
        }
    }

    override suspend fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        return if (useFirestore) {
            firestoreDAO.getHistoryWithAggregatedItems(limit)
        } else {
            roomDAO.getHistoryWithAggregatedItems(limit)
        }
    }

    override fun getHistoriesFromDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<History>> {
        return if (useFirestore) {
            firestoreDAO.getHistoriesFromDateRange(startDate, endDate)
        } else {
            roomDAO.getHistoriesFromDateRange(startDate, endDate)
        }
    }

}