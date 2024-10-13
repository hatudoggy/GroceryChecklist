package com.example.grocerychecklist.data.dao

import androidx.room.Query
import androidx.room.Transaction
import com.example.grocerychecklist.data.BaseDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import kotlinx.coroutines.flow.Flow

interface ChecklistItemDAO: BaseDAO<ChecklistItem> {

    @Query("SELECT * FROM checklistitem WHERE id = :checklistItemId LIMIT 1")
    suspend fun getChecklistItemById(checklistItemId: Int): ChecklistItemFull

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    fun getAllChecklistItems(checklistId: Int): Flow<List<ChecklistItemFull>>

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    fun getAllChecklistItemsOrderedByOrder(checklistId: Int): Flow<List<ChecklistItemFull>>

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Int): Flow<List<ChecklistItem>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.id = :checklistId
        ORDER BY item.name
    """)
    fun getAllChecklistItemsOrderedByName(checklistId: Int): Flow<List<ChecklistItemFull>>

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    fun getAllChecklistItemsOrderedByPrice(checklistId: Int): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.id = :checklistId
        AND item.name = :qName
    """)
    fun getAllChecklistItemsByName(checklistId: Int, qName: String): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.id = :checklistId
        AND item.category = :category
    """)
    fun getAllChecklistItemsByCategory(checklistId: Int, category: String): Flow<List<ChecklistItemFull>>

    @Query("SELECT MAX(`order`) FROM checklistitem WHERE checklistId = :checklistId")
    suspend fun getChecklistItemMaxOrder(checklistId: Int): Int

    @Query("SELECT COUNT(*) FROM checklistitem WHERE checklistId = :checklistId")
    suspend fun aggregateTotalChecklistItems(checklistId: Int): Int

    @Query("""
        SELECT SUM(item.price)
        FROM checklistitem
        JOIN item on checklistitem.itemId = item.id
        WHERE checklistitem.checklistId = :checklistId
    """)
    suspend fun aggregateTotalChecklistItemPrice(checklistId: Int): Double?
}