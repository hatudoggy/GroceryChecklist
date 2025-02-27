package com.example.grocerychecklist.viewmodel.auth

sealed interface AuthLoginEvent {
    data object NavigateBack: AuthLoginEvent
    data object Login: AuthLoginEvent
}