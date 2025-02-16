package com.example.grocerychecklist.viewmodel.checklist


data class ChecklistStartState (
    // Items state
    val items: List<ChecklistData> = emptyList(),
    val filteredItems: List<ChecklistData> = emptyList(),
    val searchQuery: String = "",
    val selectedItem: ChecklistData? = null,

    // Filter state
    val selectedChip: FilterType = FilterType.ALL,

    // Pricing
    val totalPrice: Double = 0.00,

    // UI Toggles
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,
    val isCheckoutOpen: Boolean = false,
)