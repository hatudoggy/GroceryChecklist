package com.example.grocerychecklist.viewmodel.settings

import androidx.compose.runtime.Stable

@Stable
data class SettingsMainState (
    // Main data
    val userName: String = "Guest",
    val userEmail: String = "Not Logged In",
    val isLoggedIn: Boolean = false,
    val isSigningOut: Boolean = false,
    val error: String? = null,
    val isPasswordReset: Boolean = false,
    val isEmailUpdateSent: Boolean = false,
    val pendingEmail: String? = null,
    val showReauthDialog: Boolean = false,
    val showEmailDialog: Boolean = false,

    // UI Checks
    val isBottomModalOpen: Boolean = false
)