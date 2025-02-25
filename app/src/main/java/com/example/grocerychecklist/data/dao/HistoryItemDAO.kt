package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import kotlinx.coroutines.flow.Flow
import java.util.Locale.Category

@Dao
interface HistoryItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long>

    @Query("SELECT * FROM historyitem WHERE id = :historyItemId LIMIT 1")
    suspend fun getHistoryItemById(historyItemId: Long): HistoryItem

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId")
    fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId ORDER BY :order")
    fun getAllHistoryItemsOrderFilter(historyId: Long, order: ChecklistItemOrder): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND isChecked = :isChecked ORDER BY :order")
    fun getAllHistoryItemsOrderAndCheckedFilter(historyId: Long, order: ChecklistItemOrder, isChecked: Boolean): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND name = :qName")
    fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND category = :category")
    fun getAllHistoryItemsByCategory(historyId: Long, category: Category): Flow<List<HistoryItem>>

    @Query("SELECT COUNT(*) FROM historyitem WHERE historyId = :historyId")
    suspend fun aggregateTotalHistoryItems(historyId: Long): Int

    @Query("""
        SELECT SUM(price)
        FROM historyitem
        WHERE historyId = :historyId
    """)
    suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double?

    @Query("""
        SELECT SUM(price * quantity)
        FROM historyItem
        INNER JOIN history ON historyItem.historyId = history.id
        WHERE strftime('%Y-%m', datetime(history.createdAt, 'unixepoch')) = :date
        AND historyItem.isChecked = 1
    """)
    fun aggregateTotalPriceMonth(date: String): Flow<Double?>


    @Query("""
        SELECT 
            SUM(historyItem.price * historyItem.quantity) AS sumOfPrice, 
            SUM(historyItem.quantity) AS totalItems,
            historyItem.category AS category
        FROM historyItem
        INNER JOIN history ON historyItem.historyId = history.id
        WHERE strftime('%Y-%m', datetime(history.createdAt, 'unixepoch')) = :date
        AND historyItem.isChecked = 1
        GROUP BY historyItem.category
        ORDER BY sumOfPrice DESC
    """)
    fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>>
}