package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist

sealed interface ChecklistMainEvent {
    // UI Toggles
    data class NavigateChecklist(val checklistId: Long, val checklistName: String) : ChecklistMainEvent
    data object ToggleDrawer : ChecklistMainEvent
    data object ToggleIconPicker : ChecklistMainEvent
    data object ToggleDeleteDialog : ChecklistMainEvent

    // State Changes
    data class SearchQueryEvent(val query: String) : ChecklistMainEvent
    data class SetNewChecklist(val checklist: ChecklistInput?) : ChecklistMainEvent
    data class SetEditingChecklist(val checklist: Checklist?) : ChecklistMainEvent

    // Repository Changes
    data class AddChecklist(val input: ChecklistInput) : ChecklistMainEvent
    data class DeleteChecklist(val checklist: Checklist) : ChecklistMainEvent
    data class UpdateChecklist(val checklist: Checklist) : ChecklistMainEvent

    data object LoadChecklists : ChecklistMainEvent
}