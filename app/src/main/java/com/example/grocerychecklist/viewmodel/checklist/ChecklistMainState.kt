package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable

@Stable
data class ChecklistMainState(
    val isAddingChecklist: Boolean = false,
    val checklistName: String = "",
    val checklistDescription: String = "",
    val isDrawerOpen: Boolean = false,
    val isIconPickerOpen: Boolean = false
)
