package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory

fun NavGraphBuilder.dashboardDestination(
    navController: NavController
) {
    composable<Routes.DashboardMain> {
        val dashboardMainViewModel = viewModel<DashboardMainViewModel>(
            factory = viewModelFactory {
                DashboardMainViewModel(GroceryChecklistApp.appModule.checklistRepository)
            }
        )
        val state by dashboardMainViewModel.state.collectAsState()
        DashboardMainScreen(
            state = state,
            onEvent = dashboardMainViewModel::onEvent
        )
    }
    composable<Routes.DashboardBreakdown> {
        DashboardBreakdownScreen()
    }
}