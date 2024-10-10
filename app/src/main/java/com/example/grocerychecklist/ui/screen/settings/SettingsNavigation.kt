package com.example.grocerychecklist.ui.screen.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes


fun NavGraphBuilder.settingsDestination() {
    composable<Routes.SettingsMain> {
        SettingsMainScreen()
    }
}