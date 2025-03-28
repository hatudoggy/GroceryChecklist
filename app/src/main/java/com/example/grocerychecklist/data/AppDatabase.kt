package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.grocerychecklist.data.dao.roomImpl.ChecklistDAOImpl
import com.example.grocerychecklist.data.dao.roomImpl.ChecklistItemDAOImpl
import com.example.grocerychecklist.data.dao.roomImpl.DatabaseDao
import com.example.grocerychecklist.data.dao.roomImpl.HistoryDAOImpl
import com.example.grocerychecklist.data.dao.roomImpl.HistoryItemDAOImpl
import com.example.grocerychecklist.data.dao.roomImpl.ItemDAOImpl
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.model.Item

@Database(entities = [
    Checklist::class,
    ChecklistItem::class,
    Item::class,
    History::class,
    HistoryItem::class,
], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun databaseDAO(): DatabaseDao
    abstract fun checklistDAO(): ChecklistDAOImpl
    abstract fun checklistItemDAO(): ChecklistItemDAOImpl
    abstract fun itemDAO(): ItemDAOImpl
    abstract fun historyDAO(): HistoryDAOImpl
    abstract fun historyItemDAO(): HistoryItemDAOImpl

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grocery-app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}