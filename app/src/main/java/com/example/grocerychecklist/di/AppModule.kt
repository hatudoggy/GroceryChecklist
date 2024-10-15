package com.example.grocerychecklist.di

import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.DatabaseRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository

interface AppModule {
    val db: AppDatabase
    val databaseRepository: DatabaseRepository
    val checklistRepository: ChecklistRepository
    val checklistItemRepository: ChecklistItemRepository
    val itemRepository: ItemRepository
    val historyRepositoryRepository: HistoryRepository
    val historyItemRepository: HistoryItemRepository
}