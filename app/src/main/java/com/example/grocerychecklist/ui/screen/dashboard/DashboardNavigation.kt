package com.example.grocerychecklist.ui.screen.dashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.serialization.Serializable

fun NavGraphBuilder.dashboardDestination(navController: NavController) {
    composable<Routes.DashboardMain> {
        DashboardMainScreen(navController)
    }
    composable<Routes.DashboardBreakdown> {
        DashboardBreakdownScreen()
    }
}