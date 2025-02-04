package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
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
                ChecklistMainViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        val state by checklistMainViewModel.state.collectAsState()
        ChecklistMainScreen(
            state = state,
            onEvent = checklistMainViewModel::onEvent
        )
    }
    composable<Routes.ChecklistDetail> {
        val checklistDetailViewModel = viewModel<ChecklistDetailViewModel>(
            factory = viewModelFactory {
                ChecklistDetailViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        ChecklistDetailScreen(
            onEvent = checklistDetailViewModel::onEvent
        )
    }
    composable<Routes.ChecklistView> {
        val checklistViewViewModel = viewModel<ChecklistViewViewModel>(
            factory = viewModelFactory {
                ChecklistViewViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        ChecklistViewScreen(
            onEvent = checklistViewViewModel::onEvent
        )
    }
    composable<Routes.ChecklistEdit> {
        val checklistEditViewModel = viewModel<ChecklistEditViewModel>(
            factory = viewModelFactory {
                ChecklistEditViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        val state by checklistEditViewModel.state.collectAsState()
        ChecklistEditScreen(
            state = state,
            onEvent = checklistEditViewModel::onEvent
        )
    }
    composable<Routes.ChecklistStart> {
        val checklistStartViewModel = viewModel<ChecklistStartViewModel>(
            factory = viewModelFactory {
                ChecklistStartViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        ChecklistStartScreen(
            viewModel = checklistStartViewModel,
            onEvent = checklistStartViewModel::onEvent
        )
    }
}