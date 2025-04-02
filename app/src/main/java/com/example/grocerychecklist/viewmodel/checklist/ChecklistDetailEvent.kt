package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistDetailEvent {
    data object NavigateBack: ChecklistDetailEvent
    data object NavigateViewMode: ChecklistDetailEvent
    data object NavigateStartMode: ChecklistDetailEvent
}