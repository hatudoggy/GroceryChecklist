package com.example.grocerychecklist.viewmodel.history

import ItemCategory

sealed interface HistoryDetailEvent {
    data object NavigateBack: HistoryDetailEvent
    data class SelectCategory(val category: ItemCategory): HistoryDetailEvent
}