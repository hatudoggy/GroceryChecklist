package com.example.grocerychecklist.ui.screen.auth

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.auth.AuthLoginViewModel
import com.example.grocerychecklist.viewmodel.auth.AuthMainViewModel
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterViewModel
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
                AuthLoginViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
//        val state by checklistMainViewModel.state.collectAsState()
        AuthLoginScreen(
//            state = state,
            onEvent = authLoginViewModel::onEvent
        )
    }
    composable<Routes.AuthRegister> {
        val authRegisterViewModel = viewModel<AuthRegisterViewModel>(
            factory = viewModelFactory {
                AuthRegisterViewModel(GroceryChecklistApp.appModule.navigator)
            }
        )
//        val state by checklistMainViewModel.state.collectAsState()
        AuthRegisterScreen(
//            state = state,
            onEvent = authRegisterViewModel::onEvent
        )
    }
}