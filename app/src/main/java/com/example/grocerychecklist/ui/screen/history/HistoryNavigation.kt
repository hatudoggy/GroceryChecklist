package com.example.grocerychecklist.ui.screen.history

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel
import com.example.grocerychecklist.viewmodel.history.HistoryMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.historyDestination() {
    composable<Routes.HistoryMain> {

        val historyMainViewModel = viewModel<HistoryMainViewModel>(
            factory = viewModelFactory {
                HistoryMainViewModel(
                    appModule.navigator, appModule.historyRepository
                )
            }
        )
        val state by historyMainViewModel.state.collectAsState()

        HistoryMainScreen(
            state = state, onEvent = historyMainViewModel::onEvent
        )
    }

    composable<Routes.HistoryDetail> { entry ->
        val route = entry.toRoute<Routes.HistoryDetail>()
        val historyDetailViewModel = viewModel<HistoryDetailViewModel>(
            factory = viewModelFactory {
                HistoryDetailViewModel(
                    checklistName = route.checklistName,
                    historyId = route.historyId,
                    navigator = appModule.navigator,
                    historyRepository = appModule.historyItemRepository,
                )
            }
        )
        HistoryDetailScreen(
            viewModel = historyDetailViewModel
        )
    }
}