package com.example.grocerychecklist.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator

class AuthLoginViewModel(
    private val navigator: Navigator
): ViewModel() {
    fun onEvent(event: AuthLoginEvent) {
        when(event) {
            AuthLoginEvent.NavigateBack -> { navigator.popBackStack() }
            AuthLoginEvent.Login -> {}
        }
    }
}