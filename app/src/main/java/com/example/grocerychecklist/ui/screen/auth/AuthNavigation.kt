package com.example.grocerychecklist.ui.screen.auth

import AuthRegisterViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.auth.AuthLoginViewModel
import com.example.grocerychecklist.viewmodel.auth.AuthMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.authDestination() {
    composable<Routes.AuthMain> {
        val authMainViewModel = viewModel<AuthMainViewModel>(
            factory = viewModelFactory {
                AuthMainViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
        AuthMainScreen(
            onEvent = authMainViewModel::onEvent
        )
    }
    composable<Routes.AuthLogin> {
        val authLoginViewModel = viewModel<AuthLoginViewModel>(
            factory = viewModelFactory {
                AuthLoginViewModel(GroceryChecklistApp.appModule.navigator, GroceryChecklistApp.appModule.accountService)
            }
        )
        val state by authLoginViewModel.uiState.collectAsState()
        AuthLoginScreen(
            state = state,
            onEvent = authLoginViewModel::onEvent
        )
    }
    composable<Routes.AuthRegister> {
        val authRegisterViewModel = viewModel<AuthRegisterViewModel>(
            factory = viewModelFactory {
                AuthRegisterViewModel(GroceryChecklistApp.appModule.navigator, GroceryChecklistApp.appModule.accountService)
            }
        )
        val state by authRegisterViewModel.uiState.collectAsState()
        AuthRegisterScreen(
            state = state,
            onEvent = authRegisterViewModel::onEvent
        )
    }
}