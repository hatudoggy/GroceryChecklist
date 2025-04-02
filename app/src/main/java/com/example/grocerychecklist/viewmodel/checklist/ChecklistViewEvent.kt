package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.ui.screen.checklist.ChecklistEditFormInputs

sealed interface ChecklistViewEvent {
    // Navigation Events
    data object NavigateBack: ChecklistViewEvent

    // UI Toggles
    data object OpenDrawer: ChecklistViewEvent
    data object CloseDrawer: ChecklistViewEvent
    data class OpenActionMenu(val item: ChecklistData): ChecklistViewEvent
    data object CloseActionMenu: ChecklistViewEvent
    data object OpenDeleteDialog: ChecklistViewEvent
    data object CloseDeleteDialog: ChecklistViewEvent

    // Input Event
//    data class SelectItem(val item: ChecklistItemFull): ChecklistEditEvent
    data object ClearSelectedItem: ChecklistViewEvent
    data class AddChecklistItem(val formInputs: ChecklistEditFormInputs): ChecklistViewEvent
    data class EditChecklistItem(val checklistId: Long, val formInputs: ChecklistEditFormInputs): ChecklistViewEvent
    data class DeleteChecklistItem(val checklistId: Long): ChecklistViewEvent
    data class DeleteChecklistItemAndItem(val checklistId: Long, val itemId: Long): ChecklistViewEvent

    // Filter Changes
    data class SetSearchQuery(val query: String): ChecklistViewEvent
}