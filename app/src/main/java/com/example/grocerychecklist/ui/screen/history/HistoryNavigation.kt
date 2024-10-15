package com.example.grocerychecklist.ui.screen.history

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel


fun NavGraphBuilder.historyDestination() {
    composable<Routes.HistoryMain> {
        HistoryMainScreen()
    }

    composable<Routes.HistoryDetail> {
        HistoryDetailScreen(HistoryDetailViewModel())
    }
}