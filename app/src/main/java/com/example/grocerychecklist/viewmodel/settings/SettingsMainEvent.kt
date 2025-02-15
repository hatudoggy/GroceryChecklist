package com.example.grocerychecklist.viewmodel.settings

sealed interface SettingsMainEvent {
    // UI Toggles
    data object ToggleBottomModal : SettingsMainEvent
}