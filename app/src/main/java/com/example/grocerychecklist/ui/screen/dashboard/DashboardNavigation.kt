package com.example.grocerychecklist.ui.screen.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.DashboardScreen
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.serialization.Serializable

fun NavGraphBuilder.dashboardDestination() {
    composable<Routes.DashboardMain> {
        DashboardScreen()
    }
    composable<Routes.DashboardBreakdown> {
        DashboardBreakdownScreen()
    }
}