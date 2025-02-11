package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption

sealed interface ChecklistMainEvent {
    data object ToggleDrawer: ChecklistMainEvent
    data object CloseDrawer: ChecklistMainEvent
    data object OpenDrawer: ChecklistMainEvent
    data object ToggleIconPicker: ChecklistMainEvent
    data object CloseIconPicker: ChecklistMainEvent
    data object OpenIconPicker: ChecklistMainEvent
    data object NavigateChecklist: ChecklistMainEvent

    data class UpdateChecklistName(val name: String): ChecklistMainEvent
    data class UpdateChecklistDescription(val description: String): ChecklistMainEvent
    data class UpdateChecklistIcon(val icon: IconOption, val color: ColorOption): ChecklistMainEvent
}