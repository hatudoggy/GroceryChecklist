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

    // UI Checks
    val isBottomModalOpen: Boolean = false
)