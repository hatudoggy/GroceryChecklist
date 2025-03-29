package com.example.grocerychecklist.viewmodel.checklist


data class ChecklistEditState (
    val checklistName: String = "",
    // Items state
    val items: List<ChecklistData> = emptyList(),
    val searchQuery: String = "",
    val selectedItem: ChecklistData? = null,

    // UI Toggles
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,

)

