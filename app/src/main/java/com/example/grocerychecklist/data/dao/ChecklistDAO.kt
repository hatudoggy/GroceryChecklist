package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.grocerychecklist.data.BaseDAO
import com.example.grocerychecklist.data.model.Checklist
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDAO: BaseDAO<Checklist> {

    @Query("SELECT * FROM checklist WHERE id = :checklistId LIMIT 1")
    suspend fun getChecklistById(checklistId: Long): Checklist

    @Query("SELECT * FROM checklist ORDER BY lastOpenedAt")
    fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>>

}