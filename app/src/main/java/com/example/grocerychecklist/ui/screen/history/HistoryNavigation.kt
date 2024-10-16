package com.example.grocerychecklist.ui.screen.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.dashboard.DashboardBreakdownScreen
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel


fun NavGraphBuilder.historyDestination(navController: NavController) {
    composable<Routes.HistoryMain> {
        HistoryMainScreen(navController)
    }

    composable<Routes.HistoryDetail> {
        HistoryDetailScreen(HistoryDetailViewModel())
    }
}