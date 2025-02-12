package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist

sealed interface ChecklistMainEvent {
    // UI Toggles
    data object ToggleDrawer : ChecklistMainEvent
    data object CloseDrawer : ChecklistMainEvent
    data object OpenDrawer : ChecklistMainEvent
    data object ToggleIconPicker : ChecklistMainEvent
    data object CloseIconPicker : ChecklistMainEvent
    data object OpenIconPicker : ChecklistMainEvent
    data object NavigateChecklist : ChecklistMainEvent
    data class ToggleActionMenu(val checklist: Checklist?) : ChecklistMainEvent
    data object ToggleDeleteDialog : ChecklistMainEvent

    // State Changes
    data class SearchChecklist(val query: String) : ChecklistMainEvent
    data class UpdateChecklistName(val name: String) : ChecklistMainEvent
    data class UpdateChecklistDescription(val description: String) : ChecklistMainEvent
    data class UpdateChecklistIcon(val icon: IconOption, val color: ColorOption) :
        ChecklistMainEvent

    // Repository Changes
    data class AddChecklist(val checklist: ChecklistInput) : ChecklistMainEvent
    data class DeleteChecklist(val checklist: Checklist?) : ChecklistMainEvent
}