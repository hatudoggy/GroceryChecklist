package com.example.grocerychecklist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocerychecklist.data.model.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDAO {

    @Insert
    suspend fun insert(history: History): Long

    @Query("SELECT * FROM history WHERE id = :historyId LIMIT 1")
    suspend fun getHistoryById(historyId: Int): History

    @Query("SELECT * FROM history ORDER BY createdAt")
    fun getAllHistoriesOrderedByCreatedAt(): Flow<List<History>>
}