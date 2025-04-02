package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ChecklistRepository(
    private val checklistDAO: ChecklistDAO
) {
    suspend fun addChecklist(checklistInput: ChecklistInput): Long {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val checklist = Checklist(
            name = checklistInput.name,
            description = checklistInput.description,
            icon = checklistInput.icon,
            iconBackgroundColor = checklistInput.iconBackgroundColor,
            createdAt = currentDateTime,
            updatedAt = currentDateTime,
            lastOpenedAt = currentDateTime,
            lastShopAt = currentDateTime
        )

        return checklistDAO.insert(checklist)
    }

    suspend fun updateChecklist(id: Long, checklistInput: ChecklistInput) {
        val checklist = checklistDAO.getChecklistById(id).first()
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedChecklist = checklist.copy(
            name = checklistInput.name,
            description = checklistInput.description,
            icon = checklistInput.icon,
            iconBackgroundColor = checklistInput.iconBackgroundColor,
            updatedAt = currentDateTime
        )

        checklistDAO.update(updatedChecklist)
    }

    suspend fun updateChecklistLastOpenedAt(checklistId: Long) {
        val currentDateTime = DateUtility.getCurrentDateTime()
        updateChecklistDate(checklistId, lastOpenedAt = currentDateTime)
    }

    suspend fun updateChecklistLastShoppedAt(checklistId: Long) {
        val currentDateTime = DateUtility.getCurrentDateTime()
        updateChecklistDate(checklistId, lastShopAt = currentDateTime)
    }

    private suspend fun updateChecklistDate(checklistId: Long, lastOpenedAt: LocalDateTime? = null, lastShopAt: LocalDateTime? = null) {
        val checklist = checklistDAO.getChecklistById(checklistId)
        checklist.let { it.map {
            val newChecklist = it.copy(
                lastOpenedAt = lastOpenedAt ?: it.lastOpenedAt,
                lastShopAt = lastShopAt ?: it.lastShopAt
            )
            checklistDAO.update(newChecklist)
         }
        }
    }

    suspend fun deleteChecklist(checklist: Checklist) {
        checklistDAO.delete(checklist)
    }

    fun getChecklist(id: Long): Flow<Checklist> {
        return checklistDAO.getChecklistById(id)
    }

    fun getChecklistWithDetails(id: Long): Flow<ChecklistDetails> {
        return checklistDAO.getChecklistWithDetails(id)
    }

    fun getChecklists(): Flow<List<Checklist>> {
        return checklistDAO.getAllChecklistsOrderedByLastOpenedAt()
    }
}

data class ChecklistDetails(
    val id: Long,
    val name: String,
    val description: String,
    val icon: IconOption,
    val iconBackgroundColor: ColorOption,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val lastOpenedAt: LocalDateTime?,
    val lastShopAt: LocalDateTime?,
    val itemCount: Int,
    val totalPrice: Double
)