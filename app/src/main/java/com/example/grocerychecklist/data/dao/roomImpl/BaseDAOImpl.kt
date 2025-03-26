package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BaseDAOImpl<T> {

    @Insert
    suspend fun insert(obj: T): Long

    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

}