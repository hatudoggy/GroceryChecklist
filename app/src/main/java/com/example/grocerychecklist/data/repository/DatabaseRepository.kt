package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.dao.DatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepository(
    private val db: AppDatabase,
    private val databaseDao: DatabaseDao
) {

    suspend fun clearRoomDB() = withContext(Dispatchers.IO){
        db.runInTransaction{
            db.clearAllTables()
            databaseDao.clearPrimaryKeyIndex()
        }
    }

}