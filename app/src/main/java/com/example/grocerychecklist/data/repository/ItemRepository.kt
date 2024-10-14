package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow

enum class ItemOrder {
    CreatedAt,
    Name,
    Price,
}

class ItemRepository(
    private val itemDAO: ItemDAO
) {

    suspend fun addItem(itemInput: ItemInput) {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val item = Item(
            name = itemInput.name,
            price = itemInput.price,
            category = itemInput.category,
            measureType = itemInput.measureType,
            measureValue = itemInput.measureValue,
            photoRef = itemInput.photoRef,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )

        itemDAO.insert(item)
    }

    suspend fun updateItem(id: Long, itemInput: ItemInput) {
        val item = itemDAO.getItemById(id)
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedItem = item.copy(
            name = itemInput.name,
            price = itemInput.price,
            category = itemInput.category,
            measureType = itemInput.measureType,
            measureValue = itemInput.measureValue,
            photoRef = itemInput.photoRef,
            updatedAt = currentDateTime
        )

        itemDAO.update(updatedItem)
    }

    suspend fun deleteItem(item: Item) {
        itemDAO.delete(item)
    }

    suspend fun getItem(id: Long): Item {
        return itemDAO.getItemById(id)
    }

    fun getItems(orderBy: ItemOrder): Flow<List<Item>> {
        return when (orderBy) {
            ItemOrder.CreatedAt -> itemDAO.getAllItemsOrderedByCreatedAt()
            ItemOrder.Name -> itemDAO.getAllItemsOrderedByName()
            ItemOrder.Price -> itemDAO.getAllItemsOrderedByPrice()
        }
    }

    fun searchItems(searchQuery: String): Flow<List<Item>> {
        return itemDAO.getAllItemsByName(searchQuery)
    }
}