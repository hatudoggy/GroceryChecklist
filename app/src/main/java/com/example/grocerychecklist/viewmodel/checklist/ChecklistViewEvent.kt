package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistViewEvent {
    data object NavigateBack: ChecklistViewEvent
}