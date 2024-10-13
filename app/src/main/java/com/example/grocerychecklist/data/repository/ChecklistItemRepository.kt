package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

enum class ChecklistItemOrder {
    Order,
    Name,
    Price,
}

class ChecklistItemRepository(
    private val checklistItemDAO: ChecklistItemDAO,
    private val itemDAO: ItemDAO
) {

    suspend fun addChecklistItem(checklistId: Int, checklistItemInput: ChecklistItemInput) {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val item = Item(
            name = checklistItemInput.name,
            price = checklistItemInput.price,
            category = checklistItemInput.category,
            measureType = checklistItemInput.measureType,
            measureValue = checklistItemInput.measureValue,
            photoRef = checklistItemInput.photoRef,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )
        val itemId = itemDAO.insert(item)

        val order = checklistItemDAO.getChecklistItemMaxOrder(checklistId) + 1
        val checklistItem = ChecklistItem(
            checklistId = checklistId,
            itemId = itemId,
            order = order,
            quantity = checklistItemInput.quantity,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )
        checklistItemDAO.insert(checklistItem)
    }

    suspend fun updateChecklistItem(checklistItemId: Int, checklistItemInput: ChecklistItemInput) {
        val checklistItem = checklistItemDAO.getChecklistItemById(checklistItemId)
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedItem = checklistItem.item.copy(
            name = checklistItemInput.name,
            price = checklistItemInput.price,
            category = checklistItemInput.category,
            measureType = checklistItemInput.measureType,
            measureValue = checklistItemInput.measureValue,
            photoRef = checklistItemInput.photoRef,
            updatedAt = currentDateTime
        )
        val updatedChecklistItem = checklistItem.checklistItem.copy(
            quantity = checklistItemInput.quantity,
            updatedAt = currentDateTime
        )

        itemDAO.update(updatedItem)
        checklistItemDAO.update(updatedChecklistItem)
    }

    suspend fun changeChecklistOrder(checklistId: Int, checklistItemId: Int, newOrder: Int) {
        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId).flattenToList()

        if (newOrder < 0 || newOrder >= checklistItems.size) {
            throw IllegalArgumentException("New order is out of bounds")
        }

        val checklistItemOrder = checklistItems.find { it.id == checklistItemId } ?: return
        val updatedList = checklistItems.toMutableList()
        updatedList.remove(checklistItemOrder)

        val adjustedItems = updatedList.mapIndexed { index, checklistItem ->
            if (index >= newOrder) {
                checklistItem.copy(order = index + 1)
            } else {
                checklistItem
            }
        }.toMutableList()

        val updatedChecklistItemOrder = checklistItemOrder.copy(order = newOrder + 1)
        adjustedItems.add(newOrder, updatedChecklistItemOrder)
        adjustedItems.forEach { checklistItemDAO.update(it) }
    }

    suspend fun deleteChecklistItem(checklistItem: ChecklistItem) {
        checklistItemDAO.delete(checklistItem)

        val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistItem.checklistId)
        checklistItems.flattenToList().forEachIndexed { index, item ->
            val updatedItem = item.copy(order = index + 1)
            checklistItemDAO.update(updatedItem)
        }
    }

    suspend fun getChecklistItem(id: Int): ChecklistItemFull {
        return checklistItemDAO.getChecklistItemById(id)
    }

    fun getChecklistItems(checklistId: Int, orderBy: ChecklistItemOrder): Flow<List<ChecklistItemFull>> {
        return when (orderBy) {
            ChecklistItemOrder.Order ->
                checklistItemDAO.getAllChecklistItemsOrderedByOrder(checklistId)

            ChecklistItemOrder.Name ->
                checklistItemDAO.getAllChecklistItemsOrderedByName(checklistId)

            ChecklistItemOrder.Price ->
                checklistItemDAO.getAllChecklistItemsOrderedByPrice(checklistId)

        }
    }

    fun searchChecklistItems(checklistId: Int, searchQuery: String): Flow<List<ChecklistItemFull>> {
        return checklistItemDAO.getAllChecklistItemsByName(checklistId,searchQuery)
    }

    suspend fun getTotalChecklistItems(checklistId: Int): Int {
        return checklistItemDAO.aggregateTotalChecklistItems(checklistId)
    }

    suspend fun getTotalChecklistItemPrice(checklistId: Int): Double {
        return checklistItemDAO.aggregateTotalChecklistItemPrice(checklistId) ?: 0.00
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()
}