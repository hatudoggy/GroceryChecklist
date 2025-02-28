package com.example.grocerychecklist.ui.screen.settings

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.settings.SettingsMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.settingsDestination() {
    composable<Routes.SettingsMain> {
        val settingsMainViewModel = viewModel<SettingsMainViewModel>(
            factory = viewModelFactory {
                SettingsMainViewModel(appModule.navigator, appModule.accountService)
            }
        )
        val state = settingsMainViewModel.state.collectAsState().value
        SettingsMainScreen(state = state, onEvent = settingsMainViewModel::onEvent)
    }
}