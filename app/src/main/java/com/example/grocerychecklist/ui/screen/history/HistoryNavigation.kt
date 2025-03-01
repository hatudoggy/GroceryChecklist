package com.example.grocerychecklist.ui.screen.history

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel
import com.example.grocerychecklist.viewmodel.history.HistoryMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.historyDestination(navController: NavController) {
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
        val historyDetailViewModel = viewModel<HistoryDetailViewModel>(
            factory = viewModelFactory {
                HistoryDetailViewModel(
                    appModule.navigator,
                    entry,
                    appModule.historyItemRepository
                )
            }
        )
        val state by historyDetailViewModel.state.collectAsState()
        HistoryDetailScreen(
            state = state,
            onEvent = historyDetailViewModel::onEvent
        )
    }
}