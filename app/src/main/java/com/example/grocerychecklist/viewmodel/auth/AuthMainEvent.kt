package com.example.grocerychecklist.viewmodel.auth

sealed interface AuthMainEvent {
    data object NavigateLogin: AuthMainEvent
    data object NavigateRegister: AuthMainEvent
    data object ContinueGuest: AuthMainEvent
}