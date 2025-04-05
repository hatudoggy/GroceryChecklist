package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory

sealed interface ChecklistDetailEvent {
    data object NavigateBack: ChecklistDetailEvent
    data class NavigateStartMode(val checklistId: Long, val checklistName: String): ChecklistDetailEvent
    data class NavigateViewMode(val checklistId: Long, val checklistName: String, val itemCategory: ItemCategory? = null): ChecklistDetailEvent

    data object LoadData: ChecklistDetailEvent
}