package com.example.grocerychecklist.ui.screen

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object DashboardMain: Routes()
    @Serializable
    data object DashboardBreakdown: Routes()

    @Serializable
    data object ChecklistMain: Routes()
    @Serializable
    data object ChecklistDetail: Routes()
    @Serializable
    data object ChecklistView: Routes()
    @Serializable
    data object ChecklistEdit: Routes()
    @Serializable
    data object ChecklistStart: Routes()

    @Serializable
    data object ItemMain: Routes()

    @Serializable
    data object HistoryMain: Routes()
    @Serializable
    data object HistoryDetail: Routes()

    @Serializable
    data object SettingsMain: Routes()
}