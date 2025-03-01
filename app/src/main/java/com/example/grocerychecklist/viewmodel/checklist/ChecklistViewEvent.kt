package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistViewEvent {
    data object NavigateBack: ChecklistViewEvent
    data class UpdateSearchQuery(val query: String): ChecklistViewEvent
}