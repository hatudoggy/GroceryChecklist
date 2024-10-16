package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grocerychecklist.data.BaseDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDAO: BaseDAO<ChecklistItem> {

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE id = :checklistItemId LIMIT 1")
    suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        ORDER BY item.name
    """)
    fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        AND item.name = :qName
    """)
    fun getAllChecklistItemsByName(checklistId: Long, qName: String): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        AND item.category = :category
    """)
    fun getAllChecklistItemsByCategory(checklistId: Long, category: String): Flow<List<ChecklistItemFull>>

    @Query("SELECT MAX(`order`) FROM checklistitem WHERE checklistId = :checklistId")
    suspend fun getChecklistItemMaxOrder(checklistId: Long): Int

    @Query("SELECT COUNT(*) FROM checklistitem WHERE checklistId = :checklistId")
    suspend fun aggregateTotalChecklistItems(checklistId: Long): Int

    @Query("""
        SELECT SUM(item.price)
        FROM checklistitem
        JOIN item on checklistitem.itemId = item.id
        WHERE checklistitem.checklistId = :checklistId
    """)
    suspend fun aggregateTotalChecklistItemPrice(checklistId: Long): Double?
}