package com.example.grocerychecklist.ui.screen

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Dashboard

fun NavGraphBuilder.dashboardDestination() {
    composable<Dashboard> {
        DashboardScreen()
    }
}