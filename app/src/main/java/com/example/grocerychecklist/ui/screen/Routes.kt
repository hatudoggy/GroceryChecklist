package com.example.grocerychecklist.ui.screen

import ItemCategory
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object AuthMain: Routes
    @Serializable
    data object AuthLogin: Routes
    @Serializable
    data object AuthRegister: Routes

    @Serializable
    data object DashboardMain: Routes
    @Serializable
    data object DashboardBreakdown: Routes

    @Serializable
    data object ChecklistMain: Routes
    @Serializable
    data class ChecklistDetail(val checklistId: Long, val checklistName: String): Routes
    @Serializable
    data class ChecklistStart(val checklistId: Long, val checklistName: String, val mode: ChecklistMode, val filterByCategory: ItemCategory?): Routes

    @Serializable
    data object ItemMain: Routes

    @Serializable
    data object HistoryMain: Routes
    @Serializable
    data class HistoryDetail(val historyId: Long, val checklistName: String): Routes

    @Serializable
    data object SettingsMain: Routes
}