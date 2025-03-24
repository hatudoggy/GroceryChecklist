package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.domain.utility.DateUtility
import kotlinx.coroutines.flow.Flow
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

        val checklistId = checklistDAO.insert(checklist)



        return checklistId
    }

    suspend fun updateChecklist(id: Long, checklistInput: ChecklistInput) {
        val checklist = checklistDAO.getChecklistById(id)
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
        checklist.let {
            val newChecklist = it.copy(
                lastOpenedAt = lastOpenedAt ?: it.lastOpenedAt,
                lastShopAt = lastShopAt ?: it.lastShopAt
            )
            checklistDAO.update(newChecklist)
        }

    }

    suspend fun deleteChecklist(checklist: Checklist) {
        checklistDAO.delete(checklist)

    }

    suspend fun getChecklist(id: Long): Checklist {
        return checklistDAO.getChecklistById(id)
    }

    fun getChecklists(): Flow<List<Checklist>> {
        return checklistDAO.getAllChecklistsOrderedByLastOpenedAt()
    }

}