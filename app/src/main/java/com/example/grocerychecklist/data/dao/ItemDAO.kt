package com.example.grocerychecklist.data.dao

import com.example.grocerychecklist.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemDAO: BaseDAO<Item> {

    suspend fun getItemById(itemId: Long): Item

    fun getAllItems(): Flow<List<Item>>

    fun getAllItemsOrderedByName(): Flow<List<Item>>

    fun getAllItemsOrderedByPrice(): Flow<List<Item>>

    fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>>

    fun getAllItemsByName(qName: String): Flow<List<Item>>

    fun getAllItemsByCategory(category: String): Flow<List<Item>>

    suspend fun deleteItemById(itemId: Long): Int

    suspend fun deleteItemByIds(vararg item: Item)

}