package com.example.grocerychecklist.ui.screen

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object DashboardMain: Routes
    @Serializable
    data object DashboardBreakdown: Routes

    @Serializable
    data object ChecklistMain: Routes
    @Serializable
    data class ChecklistDetail(val checklistId: Long): Routes
    @Serializable
    data object ChecklistView: Routes
    @Serializable
    data class ChecklistEdit(val checklistId: Long): Routes
    @Serializable
    data class ChecklistStart(val checklistId: Long): Routes

    @Serializable
    data object ItemMain: Routes

    @Serializable
    data object HistoryMain: Routes
    @Serializable
    data object HistoryDetail: Routes

    @Serializable
    data object SettingsMain: Routes
}