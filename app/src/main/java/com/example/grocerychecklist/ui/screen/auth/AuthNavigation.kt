package com.example.grocerychecklist.ui.screen.auth

import AuthRegisterViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.util.CustomBackButtonHandler
import com.example.grocerychecklist.viewmodel.auth.AuthLoginViewModel
import com.example.grocerychecklist.viewmodel.auth.AuthMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.authDestination() {
    composable<Routes.AuthMain> {
        CustomBackButtonHandler(appModule.navigator, Routes.AuthMain)
        val authMainViewModel = viewModel<AuthMainViewModel>(
            factory = viewModelFactory {
                AuthMainViewModel(
                    appModule.navigator,
                    accountService = appModule.accountService
                )
            }
        )
        AuthMainScreen(
            onEvent = authMainViewModel::onEvent
        )
    }
    composable<Routes.AuthLogin> {
        CustomBackButtonHandler(appModule.navigator, Routes.AuthMain)
        val authLoginViewModel = viewModel<AuthLoginViewModel>(
            factory = viewModelFactory {
                AuthLoginViewModel(
                    appModule.navigator,
                    appModule.accountService,
                    appModule.application)
            }
        )
        val state by authLoginViewModel.uiState.collectAsState()
        AuthLoginScreen(
            state = state,
            onEvent = authLoginViewModel::onEvent
        )
    }
    composable<Routes.AuthRegister> {
        CustomBackButtonHandler(appModule.navigator, Routes.AuthMain)
        val authRegisterViewModel = viewModel<AuthRegisterViewModel>(
            factory = viewModelFactory {
                AuthRegisterViewModel(
                    appModule.navigator,
                    appModule.accountService,
                    appModule.application)
            }
        )
        val state by authRegisterViewModel.state.collectAsState()
        AuthRegisterScreen(
            state = state,
            onEvent = authRegisterViewModel::onEvent
        )
    }
}