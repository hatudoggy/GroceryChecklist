package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.DatabaseDao
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
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
    abstract fun checklistDAO(): ChecklistDAO
    abstract fun checklistItemDAO(): ChecklistItemDAO
    abstract fun itemDAO(): ItemDAO
    abstract fun historyDAO(): HistoryDAO
    abstract fun historyItemDAO(): HistoryItemDAO

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

        fun resetInstance() {
            INSTANCE = null
        }

    }
}