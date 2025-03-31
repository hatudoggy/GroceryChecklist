package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.mapper.HistoryPriced
import com.example.grocerychecklist.data.model.History
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

@Dao
interface HistoryDAOImpl : HistoryDAO {

    @Insert
    override suspend fun insert(history: History): Long

    @Query("SELECT * FROM history WHERE id = :historyId LIMIT 1")
    override suspend fun getHistoryById(historyId: Long): History

    @Query("SELECT * FROM history ORDER BY createdAt")
    override fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>>

    @Query("""
        SELECT history.*, SUM(historyItem.price * historyItem.quantity) AS totalPrice
        FROM history
        LEFT JOIN historyItem ON history.id = historyItem.historyId
        WHERE historyItem.isChecked = 1
        GROUP BY history.id
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    override fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>>

    @Query("""SELECT SUM(price * quantity) AS sumOfPrice, COUNT(*) AS totalItems, category FROM HistoryItem WHERE historyId = :historyId GROUP BY category ORDER BY category""")
    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>>

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        return getAllHistoriesOrderedByCreatedAt().flatMapLatest { historyList ->
            combine(historyList.map { history ->
                getHistoryItemAggregated(history.id).map { aggregatedItems ->
                    val totalPrice = aggregatedItems.sumOf { it.sumOfPrice }
                    val limitedAggregatedItems = aggregatedItems.take(3)

                    HistoryMapped(
                        history = history,
                        totalPrice = totalPrice,
                        aggregatedItems = limitedAggregatedItems
                    )
                }
            }) { results: Array<HistoryMapped> -> if (limit != null) results.toList().take(limit) else results.toList() }
        }
    }

    @Query("SELECT * FROM history WHERE createdAt >= :startDate AND createdAt <= :endDate")
    override fun getHistoriesFromDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<History>>
}