package com.example.grocerychecklist.viewmodel.auth

import androidx.credentials.Credential

sealed interface AuthRegisterEvent {
    data object NavigateBack: AuthRegisterEvent
    data object NavigateToLogin: AuthRegisterEvent
    data object NavigateToDashboard: AuthRegisterEvent
    data object EmailSignUp: AuthRegisterEvent
    data class GoogleSignUp(val credential: Credential): AuthRegisterEvent
    data class FullNameChanged(val newName: String): AuthRegisterEvent
    data class EmailChanged(val newEmail: String): AuthRegisterEvent
    data class PasswordChanged(val newPassword: String): AuthRegisterEvent
    data class ConfirmPasswordChanged(val newConfirmPassword: String): AuthRegisterEvent
}