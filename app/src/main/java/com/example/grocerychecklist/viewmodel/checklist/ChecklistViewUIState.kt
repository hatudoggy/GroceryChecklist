package com.example.grocerychecklist.viewmodel.checklist

data class ChecklistViewState(
    // UI Toggles
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,
    val selectedItem: ChecklistData? = null,
)

sealed interface ChecklistViewUIState{
    data class Success(
        // Items state
        val items: List<ChecklistData> = emptyList()
    ) : ChecklistViewUIState

    object Loading : ChecklistViewUIState
    object Error : ChecklistViewUIState
    object Empty : ChecklistViewUIState
}

