package com.example.grocerychecklist.data.dao


import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import kotlinx.coroutines.flow.Flow

interface ChecklistItemDAO: BaseDAO<ChecklistItem> {

    suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull

    fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>>

    fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>>

    fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>>

    fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>>

    fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>>

    fun getAllChecklistItemsByName(checklistId: Long, qName: String): Flow<List<ChecklistItemFull>>

    fun getAllChecklistItemsByCategory(checklistId: Long, category: String): Flow<List<ChecklistItemFull>>

    suspend fun getChecklistItemMaxOrder(checklistId: Long): Int

    suspend fun deleteChecklistById(checklistId: Long): Int

    suspend fun deleteChecklistByItemId(itemId: Long): Int

    suspend fun aggregateTotalChecklistItems(checklistId: Long): Int

    suspend fun aggregateTotalChecklistItemPrice(checklistId: Long): Double?

}