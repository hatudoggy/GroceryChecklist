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
import kotlinx.coroutines.flow.flowOf
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
        SELECT history.*, COALESCE(SUM(historyItem.price * historyItem.quantity), 0) AS totalPrice
        FROM history
        LEFT JOIN historyItem ON history.id = historyItem.historyId
        WHERE historyItem.isChecked = 1
        GROUP BY history.id
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    override fun getAllHistoriesOrderedLimitWithSum(limit: Int): Flow<List<HistoryPriced>>

    @Query("""
        SELECT 
        COALESCE(SUM(price * quantity), 0) AS sumOfPrice, 
        COUNT(*) AS totalItems, 
        category  
        FROM HistoryItem 
        WHERE historyId = :historyId 
        GROUP BY category 
        ORDER BY sumOfPrice DESC""")
    override fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>>

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHistoryWithAggregatedItems(limit: Int?): Flow<List<HistoryMapped>> {
        return getAllHistoriesOrderedByCreatedAt().flatMapLatest { historyList ->
            if (historyList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            combine(historyList.map { history ->
                getHistoryItemAggregated(history.id).map { aggregatedItems ->
                    HistoryMapped(
                        history = history,
                        totalPrice = aggregatedItems.sumOf { it.sumOfPrice },
                        aggregatedItems = aggregatedItems.take(3) // Limit to 3 categories
                    )
                }
            }) { results: Array<HistoryMapped> ->
                results.toList().let { list ->
                    if (limit != null) list.take(limit) else list
                }.sortedByDescending { it.history.createdAt } // Ensure proper sorting
            }
        }
    }

    @Query("SELECT * FROM history WHERE createdAt >= :startDate AND createdAt <= :endDate")
    override fun getHistoriesFromDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<History>>
}