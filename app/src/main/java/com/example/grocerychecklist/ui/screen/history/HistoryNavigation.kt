package com.example.grocerychecklist.ui.screen.history

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.dashboard.DashboardBreakdownScreen
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel
import com.example.grocerychecklist.viewmodel.history.HistoryMainViewModel


fun NavGraphBuilder.historyDestination(navController: NavController) {
    composable<Routes.HistoryMain> {
        val viewModel: HistoryMainViewModel = viewModel()
        val historyMainState by viewModel.historyMainState.collectAsState()

        HistoryMainScreen(navController, viewModel, historyMainState)
    }

    composable<Routes.HistoryDetail> {
        HistoryDetailScreen(HistoryDetailViewModel())
    }
}