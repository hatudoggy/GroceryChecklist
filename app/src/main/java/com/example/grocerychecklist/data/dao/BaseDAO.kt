package com.example.grocerychecklist.data.dao


interface BaseDAO<T> {
    suspend fun insert(obj: T): Long
    suspend fun update(obj: T)
    suspend fun delete(vararg obj: T)
}