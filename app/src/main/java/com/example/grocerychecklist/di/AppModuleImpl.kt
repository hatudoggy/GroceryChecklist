package com.example.grocerychecklist.di

import android.app.Application
import android.content.Context
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.model.service.AccountService
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.DatabaseRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.util.BackupManager
import com.google.firebase.firestore.FirebaseFirestore


class AppModuleImpl(
    private val appContext: Context
): AppModule {

    private val backupManager: BackupManager by lazy {
        BackupManager(appContext)
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override val db: AppDatabase by lazy {
        AppDatabase.getDatabase(appContext)
    }
    override val navigator: Navigator by lazy {
        Navigator()
    }

    override val accountService: AccountService by lazy {
        AccountService()
    }

    override val application: Application by lazy {
        appContext.applicationContext as Application
    }

    override val databaseRepository: DatabaseRepository by lazy {
        DatabaseRepository(db, db.databaseDAO())
    }

    override val checklistRepository: ChecklistRepository by lazy {
        ChecklistRepository(db.checklistDAO(), backupManager)
    }

    override val checklistItemRepository: ChecklistItemRepository by lazy {
        ChecklistItemRepository(db.checklistItemDAO(), db.itemDAO(), backupManager)
    }

    override val itemRepository: ItemRepository by lazy {
        ItemRepository(db.itemDAO(), firestore)
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepository(db.historyDAO(), backupManager)
    }

    override val historyItemRepository: HistoryItemRepository by lazy {
        HistoryItemRepository(db.historyItemDAO(), backupManager)
    }

}