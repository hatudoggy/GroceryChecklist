package com.example.grocerychecklist.viewmodel.auth

sealed interface AuthRegisterEvent {
    data object NavigateBack: AuthRegisterEvent
    data object Register: AuthRegisterEvent
}