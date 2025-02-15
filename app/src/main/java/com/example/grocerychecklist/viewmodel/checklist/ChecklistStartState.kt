package com.example.grocerychecklist.viewmodel.checklist


data class ChecklistStartState (
    // Items state
    val items: List<ChecklistData> = emptyList(),
    val filteredItems: List<ChecklistData> = emptyList(),
    val searchQuery: String = "",

    // Filter state
    val selectedChip: FilterType = FilterType.ALL,

    // Pricing
    val totalPrice: Double = 0.00,

    val isAddingChecklistItem: Boolean = false,
    val itemName: String = "",
    val itemCategory: String = "",
    val itemPrice: String = "",
    val itemQuantity: String = "",
)