package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDAOImpl: ChecklistItemDAO {

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE id = :checklistItemId LIMIT 1")
    override suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    override fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    override fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId ORDER BY `order`")
    override fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        ORDER BY item.name
    """)
    override fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    override fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        AND item.name = :qName
    """)
    override fun getAllChecklistItemsByName(checklistId: Long, qName: String): Flow<List<ChecklistItemFull>>

    @Transaction
    @Query("""
        SELECT * FROM checklistitem
        INNER JOIN item ON checklistItem.itemId = item.id
        WHERE checklistItem.checklistId = :checklistId
        AND item.category = :category
    """)
    override fun getAllChecklistItemsByCategory(checklistId: Long, category: String): Flow<List<ChecklistItemFull>>

    @Query("SELECT MAX(`order`) FROM checklistitem WHERE checklistId = :checklistId")
    override suspend fun getChecklistItemMaxOrder(checklistId: Long): Int

    @Query("DELETE FROM checklistitem WHERE id = :checklistId")
    override suspend fun deleteChecklistById(checklistId: Long): Int

    @Query("DELETE FROM checklistitem WHERE itemId = :itemId")
    override suspend fun deleteChecklistByItemId(itemId: Long): Int

    @Query("SELECT COUNT(*) FROM checklistitem WHERE checklistId = :checklistId")
    override suspend fun aggregateTotalChecklistItems(checklistId: Long): Int

    @Query("""
        SELECT SUM(item.price * checklistitem.quantity)
        FROM checklistitem
        JOIN item on checklistitem.itemId = item.id
        WHERE checklistitem.checklistId = :checklistId
    """)
    override suspend fun aggregateTotalChecklistItemPrice(checklistId: Long): Double?
}