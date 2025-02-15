package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist

sealed interface ChecklistMainEvent {
    // UI Toggles
    data object ToggleDrawer : ChecklistMainEvent
    data object ToggleIconPicker : ChecklistMainEvent
    data class NavigateChecklist(val checklistId: Long) : ChecklistMainEvent
    data class ToggleActionMenu(val checklist: Checklist?) : ChecklistMainEvent
    data object ToggleDeleteDialog : ChecklistMainEvent

    // State Changes
    data class SearchChecklist(val query: String) : ChecklistMainEvent
    data class SetNewChecklist(val checklist: ChecklistInput) : ChecklistMainEvent
    data class SetDeletingChecklist(val checklist: Checklist?): ChecklistMainEvent
    data class SetEditingChecklist(val checklist: Checklist?) : ChecklistMainEvent
    data object ResetNewChecklist : ChecklistMainEvent
    data object ResetDeletingChecklist : ChecklistMainEvent
    data object ResetEditingChecklist : ChecklistMainEvent

    // Repository Changes
    data class AddChecklist(val checklist: ChecklistInput) : ChecklistMainEvent
    data class DeleteChecklist(val checklist: Checklist?) : ChecklistMainEvent
    data class UpdateChecklist(val checklist: Checklist) : ChecklistMainEvent
}