package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.ui.screen.checklist.ChecklistStartFormInputs

sealed interface ChecklistStartEvent {
    // Navigation Events
    data object NavigateBack: ChecklistStartEvent

    // UI Toggles
    data object OpenDrawer: ChecklistStartEvent
    data object CloseDrawer: ChecklistStartEvent
    data class OpenActionMenu(val item: ChecklistData): ChecklistStartEvent
    data object CloseActionMenu: ChecklistStartEvent
    data object OpenDeleteDialog: ChecklistStartEvent
    data object CloseDeleteDialog: ChecklistStartEvent

    // Checkout
    data object OpenCheckout: ChecklistStartEvent
    data object CloseCheckout: ChecklistStartEvent
    data class ProceedCheckout(val items: List<ChecklistData>): ChecklistStartEvent

    // Input Event
    data object ClearSelectedItem: ChecklistStartEvent
    data class AddChecklistItem(val formInputs: ChecklistStartFormInputs): ChecklistStartEvent
    data class EditChecklistItem(val formInputs: ChecklistStartFormInputs): ChecklistStartEvent
    data class DeleteChecklistItem(val checklistId: Long): ChecklistStartEvent
    data class DeleteChecklistItemAndItem(val checklistId: Long, val itemId: Long): ChecklistStartEvent

    // Filter Changes
    data class SelectChip(val type: FilterType): ChecklistStartEvent
    data class ToggleItemCheck(val item: ChecklistData): ChecklistStartEvent
}