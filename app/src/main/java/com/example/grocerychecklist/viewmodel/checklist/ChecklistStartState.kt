package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.ui.screen.checklist.FilterType
import com.example.grocerychecklist.viewmodel.util.SubmissionState

data class ChecklistStartState(
    val checklistName: String = "",
    // UI Checks
    val selectedChecklist: Checklist? = null,
    val selectedChip: FilterType = FilterType.ALL,
    val selectedItem: ChecklistItemData? = null,
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,
    val isCheckoutOpen: Boolean = false,

    val submissionState: SubmissionState = SubmissionState.Idle,
)

sealed interface ChecklistStartUIState{
    data object Loading : ChecklistStartUIState
    data object Empty : ChecklistStartUIState
    data class Error(val message: String?) : ChecklistStartUIState
    data class Success(val checklists: List<ChecklistItemData>) : ChecklistStartUIState
}

