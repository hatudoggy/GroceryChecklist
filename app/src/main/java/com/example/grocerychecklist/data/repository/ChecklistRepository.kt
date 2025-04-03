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
    suspend fun addChecklist(checklistInput: ChecklistInput): Result<Long> {
        return try {
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

            val id = checklistDAO.insert(checklist)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateChecklist(id: Long, checklistInput: ChecklistInput): Result<Unit> {
        return try {
            val checklist = checklistDAO.getChecklistById(id).first()

            if (checklist != null) {
                val currentDateTime = DateUtility.getCurrentDateTime()

                val updatedChecklist = checklist.copy(
                    name = checklistInput.name,
                    description = checklistInput.description,
                    icon = checklistInput.icon,
                    iconBackgroundColor = checklistInput.iconBackgroundColor,
                    updatedAt = currentDateTime
                )

                checklistDAO.update(updatedChecklist)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Checklist not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateChecklistLastOpenedAt(checklistId: Long): Result<Unit> {
        val currentDateTime = DateUtility.getCurrentDateTime()
        return updateChecklistDate(checklistId, lastOpenedAt = currentDateTime)
    }

    suspend fun updateChecklistLastShoppedAt(checklistId: Long): Result<Unit> {
        val currentDateTime = DateUtility.getCurrentDateTime()
        return updateChecklistDate(checklistId, lastShopAt = currentDateTime)
    }

    private suspend fun updateChecklistDate(checklistId: Long, lastOpenedAt: LocalDateTime? = null, lastShopAt: LocalDateTime? = null): Result<Unit> {
        return try {
            val checklist = checklistDAO.getChecklistById(checklistId).first()

            if (checklist != null) {
                val newChecklist = checklist.copy(
                    lastOpenedAt = lastOpenedAt ?: checklist.lastOpenedAt,
                    lastShopAt = lastShopAt ?: checklist.lastShopAt
                )
                checklistDAO.update(newChecklist)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Checklist not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteChecklist(checklist: Checklist): Result<Unit> {
        return try {
            checklistDAO.delete(checklist)
            Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun getChecklist(id: Long): Flow<Checklist?> {
        return checklistDAO.getChecklistById(id)
    }

    fun getChecklistWithDetails(id: Long): Flow<ChecklistDetails?> {
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