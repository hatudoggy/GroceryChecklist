package com.example.grocerychecklist.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator

class AuthRegisterViewModel(
    private val navigator: Navigator
): ViewModel() {
    fun onEvent(event: AuthRegisterEvent) {
        when(event) {
            AuthRegisterEvent.NavigateBack -> { navigator.popBackStack() }
            AuthRegisterEvent.Register -> {}
        }
    }
}

