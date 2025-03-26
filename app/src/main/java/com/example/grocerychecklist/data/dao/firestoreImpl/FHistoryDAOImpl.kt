package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.time.LocalDateTime

class FHistoryDAOImpl: FBaseDAOImpl<History>(
    FirestoreCollections.HISTORIES
), HistoryDAO {

    override suspend fun insert(history: History): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoryById(historyId: Long): History {
        TODO("Not yet implemented")
    }

    override fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>> {
        return emptyFlow()
    }

    override fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>> {
        // Return an empty list
        return emptyFlow()
    }

    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>> {
        return emptyFlow()
    }

    override suspend fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        return emptyFlow()
    }

    override fun getHistoriesFromDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<History>> {
        return emptyFlow()
    }

    override fun toFirestoreModel(obj: History): Map<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): History {
        TODO("Not yet implemented")
    }

}