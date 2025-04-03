package com.example.grocerychecklist.viewmodel.history

sealed interface HistoryMainEvent {
    data class NavigateHistory(val historyId: Long, val checklistName: String): HistoryMainEvent
    data class ToggleCard(val id: Long) : HistoryMainEvent
}