package com.example.grocerychecklist.viewmodel.settings

sealed interface SettingsMainEvent {
    // UI Toggles
    data object ToggleBottomModal : SettingsMainEvent
    data object LogIn : SettingsMainEvent
    data object SignOut : SettingsMainEvent
}