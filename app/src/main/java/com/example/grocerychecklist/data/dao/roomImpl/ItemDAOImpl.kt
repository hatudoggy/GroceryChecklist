package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAOImpl: ItemDAO {

    @Query("SELECT * FROM item where id = :itemId LIMIT 1")
    override suspend fun getItemById(itemId: Long): Item

    @Query("SELECT * FROM item")
    override fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY name")
    override fun getAllItemsOrderedByName(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY price")
    override fun getAllItemsOrderedByPrice(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY createdAt")
    override fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE name = :qName")
    override fun getAllItemsByName(qName: String): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE category = :category")
    override fun getAllItemsByCategory(category: String): Flow<List<Item>>

    @Query("DELETE FROM item WHERE id = :itemId")
    override suspend fun deleteItemById(itemId: Long): Int
    //fun aggregateTotalItems(): Int
}