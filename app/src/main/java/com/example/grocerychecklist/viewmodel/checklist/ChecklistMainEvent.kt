package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable

sealed interface ChecklistMainEvent {
    data object ToggleDrawer: ChecklistMainEvent
    data object CloseDrawer: ChecklistMainEvent
    data object OpenDrawer: ChecklistMainEvent
    data object ToggleIconPicker: ChecklistMainEvent
    data object CloseIconPicker: ChecklistMainEvent
    data object OpenIconPicker: ChecklistMainEvent
    data object NavigateChecklist: ChecklistMainEvent
}