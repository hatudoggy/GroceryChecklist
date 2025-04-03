package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistDetailEvent {
    data object NavigateBack: ChecklistDetailEvent
    data class NavigateStartMode(val checklistId: Long, val checklistName: String): ChecklistDetailEvent

    data object LoadData: ChecklistDetailEvent
}