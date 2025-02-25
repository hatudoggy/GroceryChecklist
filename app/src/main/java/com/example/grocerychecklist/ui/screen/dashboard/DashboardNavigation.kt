package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.dashboard.DashboardBreakdownViewModel
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory

fun NavGraphBuilder.dashboardDestination(
) {
    composable<Routes.DashboardMain> {
        val dashboardMainViewModel = viewModel<DashboardMainViewModel>(
            factory = viewModelFactory {
                DashboardMainViewModel(
                    GroceryChecklistApp.appModule.historyRepository,
                    GroceryChecklistApp.appModule.historyItemRepository,
                    GroceryChecklistApp.appModule.navigator
                )
            }
        )
        val state by dashboardMainViewModel.state.collectAsState()
        DashboardMainScreen(
            state = state,
            onEvent = dashboardMainViewModel::onEvent
        )
    }
    composable<Routes.DashboardBreakdown> {
        val dashboardBreakdownViewModel = viewModel<DashboardBreakdownViewModel>(
            factory = viewModelFactory {
                DashboardBreakdownViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        //val state by dashboardBreakdownViewModel.state.collectAsState()
        DashboardBreakdownScreen(
            //state = state,
            viewModel = dashboardBreakdownViewModel,
            onEvent = dashboardBreakdownViewModel::onEvent
        )
    }
}