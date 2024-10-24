package com.example.grocerychecklist.viewmodel.history

sealed interface HistoryDetailEvent {
    data object NavigateBack: HistoryDetailEvent
}