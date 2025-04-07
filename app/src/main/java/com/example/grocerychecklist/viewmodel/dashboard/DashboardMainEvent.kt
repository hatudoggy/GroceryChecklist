package com.example.grocerychecklist.viewmodel.dashboard

sealed interface DashboardMainEvent {
    data object ViewMoreBtn: DashboardMainEvent
    data object NavigateHistoryMain: DashboardMainEvent
    data class NavigateHistoryDetail(val historyId: Long, val checklistName: String): DashboardMainEvent
}