package com.example.grocerychecklist.data.dao

import com.example.grocerychecklist.data.model.Checklist
import kotlinx.coroutines.flow.Flow

interface ChecklistDAO: BaseDAO<Checklist>{
    suspend fun getChecklistById(checklistId: Long): Checklist
    fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>>
}