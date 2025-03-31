package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import kotlinx.coroutines.flow.Flow
import java.time.Month
import java.util.Locale.Category

@Dao
interface HistoryItemDAOImpl : HistoryItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long>

    @Query("SELECT * FROM historyitem WHERE id = :historyItemId LIMIT 1")
    override suspend fun getHistoryItemById(historyItemId: Long): HistoryItem

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId")
    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId ORDER BY :order")
    override fun getAllHistoryItemsOrderFilter(historyId: Long, order: ChecklistItemOrder): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND isChecked = :isChecked ORDER BY :order")
    override fun getAllHistoryItemsOrderAndCheckedFilter(historyId: Long, order: ChecklistItemOrder, isChecked: Boolean): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND name = :qName")
    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND category = :category")
    override fun getAllHistoryItemsByCategory(historyId: Long, category: Category): Flow<List<HistoryItem>>

    @Query("SELECT COUNT(*) FROM historyitem WHERE historyId = :historyId")
    override suspend fun aggregateTotalHistoryItems(historyId: Long): Int

    @Query("""
        SELECT SUM(price)
        FROM historyitem
        WHERE historyId = :historyId
    """)
    override suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double?


    override fun aggregateTotalPriceMonth(month: Month): Flow<Double?>{
        return aggregateTotalPriceMonth(month.value)
    }

    @Query(
        """
        SELECT SUM(price * quantity)
        FROM historyItem
        INNER JOIN history ON historyItem.historyId = history.id
        WHERE CAST(strftime('%m', datetime(history.createdAt, 'unixepoch')) AS INTEGER) = :monthValue
        AND historyItem.isChecked = 1
    """
    )
    fun aggregateTotalPriceMonth(monthValue: Int): Flow<Double?>

    override fun aggregateCategoryBreakdownMonth(month: Month): Flow<List<HistoryItemAggregated>> {
        return aggregateCategoryBreakdownMonth(month.value)
    }

    @Query("""
        SELECT 
            SUM(historyItem.price * historyItem.quantity) AS sumOfPrice, 
            SUM(historyItem.quantity) AS totalItems,
            historyItem.category AS category
        FROM historyItem
        INNER JOIN history ON historyItem.historyId = history.id
        WHERE CAST(strftime('%m', datetime(history.createdAt, 'unixepoch')) AS INTEGER) = :monthValue
        AND historyItem.isChecked = 1
        GROUP BY historyItem.category
        ORDER BY sumOfPrice DESC
    """)
    fun aggregateCategoryBreakdownMonth(monthValue: Int): Flow<List<HistoryItemAggregated>>
}