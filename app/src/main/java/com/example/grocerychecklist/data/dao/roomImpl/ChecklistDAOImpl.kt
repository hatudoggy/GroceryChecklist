package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistItemDAOImpl
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.repository.ChecklistDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ChecklistDAOImpl: ChecklistDAO {

    @Query("SELECT * FROM checklist WHERE id = :checklistId LIMIT 1")
    override fun getChecklistById(checklistId: Long): Flow<Checklist>

    @Query("SELECT * FROM checklist ORDER BY lastOpenedAt")
    override fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>>

    @Query(
        "SELECT checklist.*, " +
                "(SELECT COUNT(*) FROM checklistitem WHERE checklistitem.checklistId = checklist.id) as itemCount, " +
                "(SELECT SUM(item.price * checklistitem.quantity) FROM checklistitem INNER JOIN item ON checklistitem.itemId = item.id WHERE checklistitem.checklistId = checklist.id) as totalPrice " +
                "FROM checklist WHERE checklist.id = :checklistId"
    )
    override fun getChecklistWithDetails(checklistId: Long): Flow<ChecklistDetails>
}