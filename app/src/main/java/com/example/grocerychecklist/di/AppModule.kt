package com.example.grocerychecklist.di

import android.app.Application
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import com.example.grocerychecklist.ui.screen.Navigator

interface AppModule {
    val db: AppDatabase
    val navigator: Navigator
    val accountService: AccountService
    val application: Application
    val checklistRepository: ChecklistRepository
    val checklistItemRepository: ChecklistItemRepository
    val itemRepository: ItemRepository
    val historyRepository: HistoryRepository
    val historyItemRepository: HistoryItemRepository
}