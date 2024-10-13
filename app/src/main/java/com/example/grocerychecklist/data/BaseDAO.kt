package com.example.grocerychecklist.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDAO<T> {

    @Insert
    suspend fun insert(vararg obj: T): Int

    @Update
    suspend fun update(vararg obj: T): Int

    @Delete
    suspend fun delete(vararg obj: T)

}