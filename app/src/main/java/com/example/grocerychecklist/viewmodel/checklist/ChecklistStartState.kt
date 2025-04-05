package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
import com.example.grocerychecklist.ui.screen.checklist.FilterType
import com.example.grocerychecklist.viewmodel.util.SubmissionState

data class ChecklistStartState(
    val checklistName: String = "",
    val mode: ChecklistMode = ChecklistMode.EDIT,
    val newChecklist: ChecklistInput = ChecklistInput(
        name = "",
        description = "",
        icon = IconOption.MAIN_GROCERY,
        iconBackgroundColor = ColorOption.CopySkyGreen,
    ),
    val editingItem: ChecklistItemData? = null,
    val selectedItems: List<Long> = emptyList(),
    val checkedItems: List<ChecklistItemData> = emptyList(),
    val isSelectionModeActive: Boolean = false,
    val selectedSortOption: ChecklistItemOrder = ChecklistItemOrder.Name,
    val isSortAscending: Boolean = true,
    val isIconPickerOpen: Boolean = false,

    val selectedChip: FilterType = FilterType.ALL,

    val isFilterBottomSheetOpen: Boolean = false,
    val selectedCategories: Set<ItemCategory> = setOf(ItemCategory.ALL),

    // UI Checks
    val isDrawerOpen: Boolean = false,
    val isActionMenuOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isCheckoutOpen: Boolean = false,
    val isChangeCategoryDialogOpen: Boolean = false,
    val isMoreActionsMenuOpen: Boolean = false,
    val isCopyDialogOpen: Boolean = false,

    val submissionState: SubmissionState = SubmissionState.Idle,
)

sealed interface ChecklistStartUIState{
    data object Loading : ChecklistStartUIState
    data object Empty : ChecklistStartUIState
    data class Error(val message: String?) : ChecklistStartUIState
    data class Success(val checklists: List<ChecklistItemData>) : ChecklistStartUIState
}

