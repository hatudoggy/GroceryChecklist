package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.model.History
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@Dao
interface HistoryDAO {

    @Insert
    suspend fun insert(history: History): Long

    @Query("SELECT * FROM history WHERE id = :historyId LIMIT 1")
    suspend fun getHistoryById(historyId: Long): History

    @Query("SELECT * FROM history ORDER BY createdAt")
    fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>>

    @Query("""SELECT SUM(price * quantity) AS sumOfPrice, COUNT(*) AS totalItems, category FROM HistoryItem WHERE historyId = :historyId GROUP BY category ORDER BY category""")
    fun getHistoryItemAggregated(historyId: Long): Flow<List<HistoryItemAggregated>>

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getHistoryWithAggregatedItems(): Flow<List<HistoryMapped>> {
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
            }) { results: Array<HistoryMapped> -> results.toList() }
        }
    }


}