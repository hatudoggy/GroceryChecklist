package com.example.grocerychecklist.di

import android.app.Application
import android.content.Context
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.dao.DAOProvider
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import com.example.grocerychecklist.data.repository.RepositoryManager
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

    private val repositoryManager by lazy {
        RepositoryManager(appContext, accountService)
    }

    override val checklistRepository: ChecklistRepository
        get() = repositoryManager.getChecklistRepository()


    override val checklistItemRepository: ChecklistItemRepository
        get() = repositoryManager.getChecklistItemRepository()

    override val itemRepository: ItemRepository
        get() = repositoryManager.getItemRepository()


    override val historyRepository: HistoryRepository
        get() = repositoryManager.getHistoryRepository()


    override val historyItemRepository: HistoryItemRepository
        get() = repositoryManager.getHistoryItemRepository()
}