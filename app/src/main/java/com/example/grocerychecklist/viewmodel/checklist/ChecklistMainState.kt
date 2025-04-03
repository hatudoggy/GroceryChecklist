package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.runtime.Stable
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.viewmodel.util.SubmissionState

@Stable
data class ChecklistMainState(
    // UI Checks
    val selectedChecklist: Checklist? = null,
    val isIconPickerOpen: Boolean = false,
    val editingChecklist: Checklist? = null,
    val newChecklist: ChecklistInput = ChecklistInput(
        name = "",
        description = ""
    ),
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val submissionState: SubmissionState = SubmissionState.Idle,
    val selectedItem: Checklist? = null,
)


