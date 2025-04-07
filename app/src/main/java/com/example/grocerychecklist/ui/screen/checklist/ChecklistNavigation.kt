package com.example.grocerychecklist.ui.screen.checklist

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.checklistDestination() {
    composable<Routes.ChecklistMain> {
        val checklistMainViewModel = viewModel<ChecklistMainViewModel>(
            factory = viewModelFactory {
                ChecklistMainViewModel(appModule.navigator, appModule.checklistRepository)
            }
        )
        ChecklistMainScreen(
            viewModel = checklistMainViewModel
        )
    }
    composable<Routes.ChecklistDetail> { entry ->
        val route = entry.toRoute<Routes.ChecklistDetail>()
        val checklistDetailViewModel = viewModel<ChecklistDetailViewModel>(
            factory = viewModelFactory {
                ChecklistDetailViewModel(route.checklistId, appModule.checklistRepository, appModule.navigator)
            }
        )
        ChecklistDetailScreen(
            viewModel = checklistDetailViewModel
        )
    }

    composable<Routes.ChecklistStart> { entry ->
        val route = entry.toRoute<Routes.ChecklistStart>()
        val checklistStartViewModel = viewModel<ChecklistStartViewModel>(
            factory = viewModelFactory {
                ChecklistStartViewModel(
                    route.checklistId,
                    route.checklistName,
                    route.filterByCategory ?: ItemCategory.ALL,
                    route.mode,
                    appModule.navigator,
                    appModule.checklistItemRepository,
                    appModule.checklistRepository,
                    appModule.historyRepository,
                    appModule.historyItemRepository
                )
            }
        )
        ChecklistStartScreen(
            viewModel = checklistStartViewModel
        )
    }
}