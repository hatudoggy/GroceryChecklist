package com.example.grocerychecklist.viewmodel.history

sealed interface HistoryMainEvent {
    data class NavigateHistory(val historyId: Long): HistoryMainEvent
    data class ToggleCard(val id: Long) : HistoryMainEvent
    data object AddMockData : HistoryMainEvent
}