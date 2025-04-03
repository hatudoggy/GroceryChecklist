package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.ui.screen.checklist.FilterType

sealed interface ChecklistStartEvent {
    data object NavigateBack : ChecklistStartEvent

    data object ToggleDrawer : ChecklistStartEvent
    data object ToggleDeleteDialog : ChecklistStartEvent
    data object ToggleCheckout: ChecklistStartEvent
    data class ToggleItemCheck(val checklistItemId: Long) : ChecklistStartEvent
    data class ToggleActionMenu(val checklist: ChecklistItemData) : ChecklistStartEvent

    // Filter Changes
    data class FilterSelection(val type: FilterType): ChecklistStartEvent
    data class SearchQueryEvent(val query: String) : ChecklistStartEvent

    data object ProceedCheckout: ChecklistStartEvent

    data class ItemSelection(val checklist: ChecklistItemData?) : ChecklistStartEvent
    data class ItemAddition(val input: ChecklistItemFormInputs) : ChecklistStartEvent
    data class ItemModification(val checklistItemId: Long, val input: ChecklistItemFormInputs) : ChecklistStartEvent
    data class ItemDeletion(val checklistItemId: Long, val groceryItemId: Long?) : ChecklistStartEvent
    data object LoadData : ChecklistStartEvent
}