package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.util.BackupManager
import kotlinx.coroutines.flow.Flow

enum class ItemOrder {
    CreatedAt,
    Name,
    Price,
}

class ItemRepository(
    private val itemDAO: ItemDAO,
    private val backupManager: BackupManager
) {

    suspend fun addItem(itemInput: ItemInput): Long {
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

        val itemId = itemDAO.insert(item)

        backupManager.triggerBackup()

        return itemId
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
        backupManager.triggerBackup()
    }

    suspend fun deleteItem(item: Item) {
        itemDAO.delete(item)
        backupManager.triggerBackup()
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