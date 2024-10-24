package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistStartEvent {
    data object NavigateBack: ChecklistStartEvent
}