package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistItemDAOImpl
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.repository.ChecklistDetails
import com.example.grocerychecklist.ui.screen.checklist.CategorySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

@Dao
interface ChecklistDAOImpl: ChecklistDAO {

    @Query("SELECT * FROM checklist WHERE id = :checklistId LIMIT 1")
    override fun getChecklistById(checklistId: Long): Flow<Checklist>

    @Query("SELECT * FROM checklist ORDER BY lastOpenedAt")
    override fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>>

    @Transaction
    @Query("SELECT * FROM checklistitem WHERE checklistId = :checklistId")
    fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>>

    override fun getChecklistWithDetails(checklistId: Long): Flow<ChecklistDetails> {
        return flow {
            val checklist = getChecklistById(checklistId).first()
            val checklistItems = getAllChecklistItems(checklistId).first()
            val itemCount = checklistItems.size
            val totalPrice = checklistItems.sumOf { it.item.price * it.checklistItem.quantity }
            val categoriesSummary = ItemCategory.entries.mapNotNull { category ->
                val categoryItemCount = checklistItems.count { it.item.category == category.name }
                CategorySummary(
                    itemCategory = category,
                    itemCount = categoryItemCount,
                    totalPrice = checklistItems.sumOf { if (it.item.category == category.name) it.item.price * it.checklistItem.quantity else 0.0 }
                ).takeIf { categoryItemCount > 0 }
            }

            emit(ChecklistDetails(
                checklist.id,
                checklist.name,
                checklist.description,
                checklist.icon,
                checklist.iconBackgroundColor,
                checklist.createdAt,
                checklist.updatedAt,
                checklist.lastOpenedAt,
                checklist.lastShopAt,
                itemCount = itemCount,
                totalPrice = totalPrice,
                categoriesSummary = categoriesSummary,
            ))
        }
    }
}