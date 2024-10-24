package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable

sealed interface ChecklistMainEvent {
    @Stable
    data class DialogState(
        val visible: Boolean = false
    )
    data object NavigateChecklist: ChecklistMainEvent
}