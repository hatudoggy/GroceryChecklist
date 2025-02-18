package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.checklistDestination() {
    composable<Routes.ChecklistMain> {
        val checklistMainViewModel = viewModel<ChecklistMainViewModel>(
            factory = viewModelFactory {
                ChecklistMainViewModel(appModule.navigator, appModule.checklistRepository)
            }
        )
        val state by checklistMainViewModel.state.collectAsState()
        ChecklistMainScreen(
            state = state,
            onEvent = checklistMainViewModel::onEvent
        )
    }
    composable<Routes.ChecklistDetail> { entry ->
        val checklistDetailViewModel = viewModel<ChecklistDetailViewModel>(
            factory = viewModelFactory {
                ChecklistDetailViewModel(appModule.checklistRepository, appModule.navigator, entry)
            }
        )
        val state by checklistDetailViewModel.state.collectAsState()
        ChecklistDetailScreen(
            state = state,
            onEvent = checklistDetailViewModel::onEvent
        )
    }
    composable<Routes.ChecklistView> {
        val checklistViewViewModel = viewModel<ChecklistViewViewModel>(
            factory = viewModelFactory {
                ChecklistViewViewModel(appModule.navigator)
            }
        )
        ChecklistViewScreen(
            checklistViewViewModel,
            onEvent = checklistViewViewModel::onEvent
        )
    }
    composable<Routes.ChecklistEdit> { entry ->
        val checklistEditViewModel = viewModel<ChecklistEditViewModel>(
            factory = viewModelFactory {
                ChecklistEditViewModel(
                    appModule.navigator, entry, appModule.checklistItemRepository
                )
            }
        )
        val state by checklistEditViewModel.state.collectAsState()
        ChecklistEditScreen(
            state = state,
            onEvent = checklistEditViewModel::onEvent
        )
    }
    composable<Routes.ChecklistStart> { entry ->
        val checklistStartViewModel = viewModel<ChecklistStartViewModel>(
            factory = viewModelFactory {
                ChecklistStartViewModel(
                    appModule.navigator,
                    entry,
                    appModule.checklistItemRepository,
                    appModule.checklistRepository,
                    appModule.historyRepository,
                    appModule.historyItemRepository
                )
            }
        )
        val state by checklistStartViewModel.state.collectAsState()
        ChecklistStartScreen(
            state = state,
            onEvent = checklistStartViewModel::onEvent
        )
    }
}