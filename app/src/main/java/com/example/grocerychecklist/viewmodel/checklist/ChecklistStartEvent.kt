package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistStartEvent {
    data object NavigateBack: ChecklistStartEvent
    data class SelectChip(val type: FilterType): ChecklistStartEvent
    data class ToggleItemCheck(val item: ChecklistData): ChecklistStartEvent
}