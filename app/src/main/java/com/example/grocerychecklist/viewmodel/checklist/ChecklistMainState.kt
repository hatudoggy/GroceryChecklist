package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.ui.component.ChecklistCategory

@Stable
data class ChecklistMainState(
    // Main data
    val checklists: List<Checklist> = emptyList(),

    // UI Checks
    val isAddingChecklist: Boolean = false,
    val isDrawerOpen: Boolean = false,
    val isIconPickerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,

    // Search
    val searchQuery: String = "",

    // For adding new checklist
    val newChecklist: ChecklistInput = ChecklistInput(
        name = "",
        description = "",
        icon = IconOption.MAIN_GROCERY,
        iconBackgroundColor = ColorOption.CopySkyGreen
    ),

    // For editing checklist
    val editingChecklist: Checklist? = null,

    // For deleting checklist
    val deletingChecklist: Checklist? = null,

    // Misc
    val checklistName: String = "",
    val checklistDescription: String = "",
    val selectedChecklist: Checklist? = null,
)
