package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grocerychecklist.data.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import java.util.Locale.Category

@Dao
interface HistoryItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(historyItems: List<HistoryItem>): List<Int>

    @Query("SELECT * FROM historyitem WHERE id = :historyItemId LIMIT 1")
    suspend fun getHistoryItemById(historyItemId: Int): HistoryItem

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId")
    fun getAllHistoryItems(historyId: Int): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId ORDER BY name")
    fun getAllHistoryItemsOrderedByName(historyId: Int): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId ORDER BY price")
    fun getAllHistoryItemsOrderedByPrice(historyId: Int): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId ORDER BY `order`")
    fun getAllHistoryItemsOrderedByOrder(historyId: Int): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND name = :qName")
    fun getAllHistoryItemsByName(historyId: Int, qName: String): Flow<List<HistoryItem>>

    @Query("SELECT * FROM historyitem WHERE historyId = :historyId AND category = :category")
    fun getAllHistoryItemsByCategory(historyId: Int, category: Category): Flow<List<HistoryItem>>

    @Query("SELECT COUNT(*) FROM historyitem WHERE historyId = :historyId")
    suspend fun aggregateTotalHistoryItems(historyId: Int): Int

    @Query("""
        SELECT SUM(price)
        FROM historyitem
        WHERE historyId = :historyId
    """)
    suspend fun aggregateTotalHistoryItemPrice(historyId: Int): Double?
}