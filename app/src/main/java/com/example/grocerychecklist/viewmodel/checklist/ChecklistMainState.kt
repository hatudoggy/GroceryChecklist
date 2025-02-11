package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.ui.component.ChecklistCategory

@Stable
data class ChecklistMainState(
    val isAddingChecklist: Boolean = false,
    val checklistName: String = "",
    val checklistDescription: String = "",
    val isDrawerOpen: Boolean = false,
    val isIconPickerOpen: Boolean = false,
    val newChecklist: ChecklistInput = ChecklistInput(
        name = "",
        description = "",
        icon = IconOption.MAIN_GROCERY,
        iconBackgroundColor = ColorOption.CopySkyGreen
    )
)
