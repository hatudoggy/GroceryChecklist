package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.ui.screen.checklist.ChecklistEditFormInputs

sealed interface ChecklistEditEvent {
    // Navigation Events
    data object NavigateBack: ChecklistEditEvent

    // UI Toggles
    data object OpenDrawer: ChecklistEditEvent
    data object CloseDrawer: ChecklistEditEvent
    data class OpenActionMenu(val item: ChecklistItemFull): ChecklistEditEvent
    data object CloseActionMenu: ChecklistEditEvent
    data object OpenDeleteDialog: ChecklistEditEvent
    data object CloseDeleteDialog: ChecklistEditEvent

    // Input Event
//    data class SelectItem(val item: ChecklistItemFull): ChecklistEditEvent
    data object ClearSelectedItem: ChecklistEditEvent
    data class AddChecklistItem(val formInputs: ChecklistEditFormInputs): ChecklistEditEvent
    data class EditChecklistItem(val formInputs: ChecklistEditFormInputs): ChecklistEditEvent
    data class DeleteChecklistItem(val item: ChecklistItem): ChecklistEditEvent

    // Filter Changes
    data class SetSearchQuery(val query: String): ChecklistEditEvent
}