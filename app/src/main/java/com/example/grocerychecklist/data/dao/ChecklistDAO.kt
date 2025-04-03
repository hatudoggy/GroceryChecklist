package com.example.grocerychecklist.data.dao

import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.repository.ChecklistDetails
import kotlinx.coroutines.flow.Flow

interface ChecklistDAO: BaseDAO<Checklist>{
    fun getChecklistById(checklistId: Long): Flow<Checklist?>
    fun getChecklistWithDetails(checklistId: Long): Flow<ChecklistDetails?>
    fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>>
}