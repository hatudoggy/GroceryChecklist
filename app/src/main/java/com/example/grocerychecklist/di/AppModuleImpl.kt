package com.example.grocerychecklist.di

import android.app.Application
import android.content.Context
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistItemDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FHistoryDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FHistoryItemDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FItemDAOImpl
import com.example.grocerychecklist.data.dao.manager.ChecklistDAOManager
import com.example.grocerychecklist.data.dao.manager.ChecklistItemDAOManager
import com.example.grocerychecklist.data.dao.manager.HistoryDAOManager
import com.example.grocerychecklist.data.dao.manager.HistoryItemDAOManager
import com.example.grocerychecklist.data.dao.manager.ItemDAOManager
import com.example.grocerychecklist.data.model.service.AccountService
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

    override val accountService: AccountService by lazy {
        AccountService()
    }

    override val application: Application by lazy {
        appContext.applicationContext as Application
    }

    private val fChecklistDAO: ChecklistDAO by lazy {
        FChecklistDAOImpl()
    }

    private val fChecklistItemDAO: ChecklistItemDAO by lazy {
        FChecklistItemDAOImpl()
    }

    private val fHistoryDAO: HistoryDAO by lazy {
        FHistoryDAOImpl()
    }

    private val fHistoryItemDAO: HistoryItemDAO by lazy {
        FHistoryItemDAOImpl()
    }

    private val fItemDAO: ItemDAO by lazy {
        FItemDAOImpl()
    }

    override val databaseRepository: DatabaseRepository by lazy {
        DatabaseRepository(db, db.databaseDAO())
    }

    override val checklistRepository: ChecklistRepository by lazy {
        ChecklistRepository(
            ChecklistDAOManager(
                db.checklistDAO(),
                fChecklistDAO,
            )
        )
    }

    override val checklistItemRepository: ChecklistItemRepository by lazy {
        ChecklistItemRepository(
            ChecklistItemDAOManager(
                db.checklistItemDAO(),
                fChecklistItemDAO
            ),
            ItemDAOManager(
                db.itemDAO(),
                fItemDAO
            )
        )
    }

    override val itemRepository: ItemRepository by lazy {
        ItemRepository(
            ItemDAOManager(
                db.itemDAO(),
                fItemDAO
            )
        )
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepository(
            HistoryDAOManager(
                db.historyDAO(),
                fHistoryDAO
            )
        )
    }

    override val historyItemRepository: HistoryItemRepository by lazy {
        HistoryItemRepository(
            HistoryItemDAOManager(
                db.historyItemDAO(),
                fHistoryItemDAO
            )
        )
    }

}