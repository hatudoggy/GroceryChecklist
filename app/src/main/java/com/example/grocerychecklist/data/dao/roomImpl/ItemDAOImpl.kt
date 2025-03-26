package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query
import com.example.grocerychecklist.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAOImpl: BaseDAOImpl<Item> {

    @Query("SELECT * FROM item where id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Long): Item

    @Query("SELECT * FROM item")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY name")
    fun getAllItemsOrderedByName(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY price")
    fun getAllItemsOrderedByPrice(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY createdAt")
    fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE name = :qName")
    fun getAllItemsByName(qName: String): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE category = :category")
    fun getAllItemsByCategory(category: String): Flow<List<Item>>

    @Query("DELETE FROM item WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long): Int

    //fun aggregateTotalItems(): Int
}