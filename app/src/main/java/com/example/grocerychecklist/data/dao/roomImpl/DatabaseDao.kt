package com.example.grocerychecklist.data.dao.roomImpl

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DatabaseDao {

    @Query("DELETE FROM sqlite_sequence")
    fun clearPrimaryKeyIndex()

}