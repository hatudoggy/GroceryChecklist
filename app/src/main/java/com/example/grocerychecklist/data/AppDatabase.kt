package com.example.grocerychecklist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.model.Checklist

@Database(entities = [
    Checklist::class
], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun checklistDAO(): ChecklistDAO
}