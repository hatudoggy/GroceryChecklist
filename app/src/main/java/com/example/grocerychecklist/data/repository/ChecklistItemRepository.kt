package com.example.grocerychecklist.data.repository

import android.R.attr.order
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

enum class ChecklistItemOrder(val order: String) {
    Order("order"),
    Name("name"),
    Price("price"),
    Date("createdAt"),
    Quantity("quantity"),
    Category("category")
}

class ChecklistItemRepository(
    private val checklistItemDAO: ChecklistItemDAO,
    private val itemDAO: ItemDAO
) {

    suspend fun addChecklistItem(checklistId: Long, checklistItemInput: ChecklistItemInput): Result<Long> {
        return try {
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

            val order = checklistItemDAO.getChecklistItemMaxOrder(checklistId).first()

            if (order == null) {
                Result.Error(Exception("Order not found"))
                throw Exception("Order not found")
            }

            val checklistItem = ChecklistItem(
                checklistId = checklistId,
                itemId = itemId,
                order = order + 1,
                quantity = checklistItemInput.quantity,
                createdAt = currentDateTime,
                updatedAt = currentDateTime
            )

            checklistItemDAO.insert(checklistItem)
            Result.Success(checklistItem.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun copyChecklistItemsToChecklist(checklistId: Long, items: Map<Long, Int>): Result<Unit> {
        return try {
            val currentDateTime = DateUtility.getCurrentDateTime()

            items.forEach { itemId ->
                val checklistItem = ChecklistItem(
                    checklistId = checklistId,
                    itemId = itemId.component1(),
                    order = 1,
                    quantity = itemId.component2(),
                    createdAt = currentDateTime,
                    updatedAt = currentDateTime
                )

                checklistItemDAO.insert(checklistItem)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateChecklistItem(checklistItemId: Long, checklistItemInput: ChecklistItemInput): Result<Unit> {
        return try {
            val checklistItem = checklistItemDAO.getChecklistItemById(checklistItemId).first()

            if (checklistItem == null) {
                return Result.Error(Exception("ChecklistItem not found"))
            }

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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateChecklistItemCategory(checklistItemId: Long, category: String): Result<Unit> {
        return try {
            val checklistItem = checklistItemDAO.getChecklistItemById(checklistItemId).first()
            if (checklistItem == null) {
                return Result.Error(Exception("ChecklistItem not found"))
            }
            val updatedItem = checklistItem.item.copy(category = category)
            itemDAO.update(updatedItem)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun changeChecklistOrder(checklistId: Long, checklistItemId: Long, newOrder: Int): Result<Unit> {
        return try {
            val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId).take(1).first()

            if (newOrder < 0 || newOrder >= checklistItems.size) {
                Result.Error(Exception("New order is out of bounds"))
                throw IllegalArgumentException("New order is out of bounds")
            }

            val checklistItemOrder = checklistItems.find { it.id == checklistItemId } ?: return Result.Error(Exception("ChecklistItem not found"))
            val updatedList = checklistItems.toMutableList()
            updatedList.remove(checklistItemOrder)
            updatedList.add(newOrder - 1, checklistItemOrder)

            val adjustedItems = updatedList.mapIndexed { index, checklistItem ->
                checklistItem.copy(order = index + 1)
            }.toMutableList()

            adjustedItems.forEach { checklistItemDAO.update(it) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteChecklistItem(checklistItem: ChecklistItem): Result<Unit> {
        return try {
            checklistItemDAO.delete(checklistItem)

            val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistItem.checklistId)
            checklistItems.take(1).first().forEachIndexed { index, item ->
                val updatedItem = item.copy(order = index + 1)
                checklistItemDAO.update(updatedItem)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteChecklistItem(checklistId: Long): Result<Unit> {
        return try {
            checklistItemDAO.deleteChecklistById(checklistId)

            val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
            checklistItems.take(1).first().forEachIndexed { index, item ->
                val updatedItem = item.copy(order = index + 1)
                checklistItemDAO.update(updatedItem)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteChecklistItemAndItem(checklistId: Long, itemId: Long): Result<Unit> {
        return try {
            checklistItemDAO.deleteChecklistById(checklistId)

            itemDAO.deleteItemById(itemId)

            val checklistItems = checklistItemDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
            checklistItems.take(1).first().forEachIndexed { index, item ->
                val updatedItem = item.copy(order = index + 1)
                checklistItemDAO.update(updatedItem)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getChecklistItem(id: Long): Flow<ChecklistItemFull?> {
        return checklistItemDAO.getChecklistItemById(id)
    }

    fun getChecklistItems(checklistId: Long, orderBy: ChecklistItemOrder): Flow<List<ChecklistItemFull>> {
        return when (orderBy) {
            ChecklistItemOrder.Order ->
                checklistItemDAO.getAllChecklistItemsOrderedByOrder(checklistId)

            ChecklistItemOrder.Name ->
                checklistItemDAO.getAllChecklistItemsOrderedByName(checklistId)

            ChecklistItemOrder.Price ->
                checklistItemDAO.getAllChecklistItemsOrderedByPrice(checklistId)

            ChecklistItemOrder.Date ->
                checklistItemDAO.getAllChecklistItemsOrderedByPrice(checklistId)

            ChecklistItemOrder.Quantity ->
                checklistItemDAO.getAllChecklistItems(checklistId).map { it.sortedBy { it.checklistItem.quantity } }

            ChecklistItemOrder.Category ->
                checklistItemDAO.getAllChecklistItems(checklistId).map { it.sortedBy { it.item.category } }
        }
    }

    fun searchChecklistItems(checklistId: Long, searchQuery: String): Flow<List<ChecklistItemFull>> {
        return checklistItemDAO.getAllChecklistItemsByName(checklistId,searchQuery)
    }

    fun getTotalChecklistItems(checklistId: Long): Flow<Int?> {
        return checklistItemDAO.aggregateTotalChecklistItems(checklistId)
    }

    fun getTotalChecklistItemPrice(checklistId: Long): Flow<Double?> {
        return checklistItemDAO.aggregateTotalChecklistItemPrice(checklistId)
    }

}