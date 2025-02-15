package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.data.model.ChecklistItemFull

data class ChecklistEditState (
    // Items state
    val items: List<ChecklistItemFull> = emptyList(),
    val searchQuery: String = "",
    val selectedItem: ChecklistItemFull? = null,

    // UI Toggles
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isEditingItem: Boolean = false,

)

