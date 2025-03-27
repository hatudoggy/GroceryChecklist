package com.example.grocerychecklist.viewmodel.auth

import androidx.credentials.Credential

sealed interface AuthLoginEvent {
    data object NavigateBack: AuthLoginEvent
    data object NavigateToRegister: AuthLoginEvent
    data object Login: AuthLoginEvent
    data class GoogleLogIn(val credential: Credential): AuthLoginEvent
    data class UpdateEmail(val newEmail: String): AuthLoginEvent
    data class UpdatePassword(val newPassword: String): AuthLoginEvent
}