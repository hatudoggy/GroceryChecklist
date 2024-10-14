package com.example.grocerychecklist.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDAO<T> {

    @Insert
    suspend fun insert(obj: T): Long

    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

}