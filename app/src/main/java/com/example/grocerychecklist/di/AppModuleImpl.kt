package com.example.grocerychecklist.di

import android.content.Context
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.DatabaseRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import com.example.grocerychecklist.ui.screen.Navigator


class AppModuleImpl(
    private val appContext: Context
): AppModule {

    override val db: AppDatabase by lazy {
        AppDatabase.getDatabase(appContext)
    }
    override val navigator: Navigator by lazy {
        Navigator()
    }

    override val databaseRepository: DatabaseRepository by lazy {
        DatabaseRepository(db, db.databaseDAO())
    }

    override val checklistRepository: ChecklistRepository by lazy {
        ChecklistRepository(db.checklistDAO())
    }

    override val checklistItemRepository: ChecklistItemRepository by lazy {
        ChecklistItemRepository(db.checklistItemDAO(), db.itemDAO())
    }

    override val itemRepository: ItemRepository by lazy {
        ItemRepository(db.itemDAO())
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepository(db.historyDAO())
    }

    override val historyItemRepository: HistoryItemRepository by lazy {
        HistoryItemRepository(db.historyItemDAO())
    }

}