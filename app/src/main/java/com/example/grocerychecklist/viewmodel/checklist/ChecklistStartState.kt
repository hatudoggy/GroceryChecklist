package com.example.grocerychecklist.viewmodel.checklist


data class ChecklistStartState (
    // Items state
    val selectedItem: ChecklistData? = null,
    val checkedItems: List<ChecklistData> = emptyList(),

    // Filter state
    val selectedChip: FilterType = FilterType.ALL,

    // UI Toggles
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,
    val isCheckoutOpen: Boolean = false,
)

sealed interface ChecklistStartUIState {
    data class Success(
        val filteredItems: List<ChecklistData>
    ): ChecklistStartUIState
    object Error: ChecklistStartUIState
    object Loading: ChecklistStartUIState
}