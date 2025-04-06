package com.example.grocerychecklist.data.repository

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.grocerychecklist.data.dao.DAOProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RepositoryManager(
    private val context: Context,
    private val authRepository: AuthRepository) {
    private var currentProvider: DAOProvider by mutableStateOf(
        DAOProvider.createProvider(context, authRepository.hasUser())
    )

    init {
        authRepository.currentAuthUser
            .onEach { authUser ->
                val useFirestore = authUser?.isAnonymous == false
                currentProvider = DAOProvider.createProvider(context, useFirestore)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun getChecklistRepository(): ChecklistRepository {
        return ChecklistRepository(currentProvider.getChecklistDAO())
    }

    fun getChecklistItemRepository(): ChecklistItemRepository {
        return ChecklistItemRepository(
            currentProvider.getChecklistItemDAO(),
            currentProvider.getItemDAO()
        )
    }

    fun getItemRepository(): ItemRepository{
        return ItemRepository(
            currentProvider.getChecklistItemDAO(),
            currentProvider.getItemDAO()
        )
    }

    fun getHistoryRepository(): HistoryRepository {
        return HistoryRepository(currentProvider.getHistoryDAO())
    }

    fun getHistoryItemRepository(): HistoryItemRepository {
        return HistoryItemRepository(currentProvider.getHistoryItemDAO())
    }
}