package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
import com.example.grocerychecklist.ui.screen.checklist.FilterType

/**
 * Sealed interface representing events that can be triggered within the Checklist screen.
 * Each event represents a user interaction or a state change within the Checklist.
 */
sealed interface ChecklistStartEvent {
    data object NavigateBack : ChecklistStartEvent

    /**
     * Event triggered when the Checklist mode changes.
     * @property mode The new Checklist mode.
     */
    data class ChangeMode(val mode: ChecklistMode) : ChecklistStartEvent
    /**
     * Event triggered when an item's selection state is toggled.
     * @property itemId The ID of the item whose selection is toggled.
     */
    data class ToggleItemSelection(val itemId: Long) : ChecklistStartEvent
    /**
     * Event triggered when all items are selected.
     */
    data object SelectAllItems : ChecklistStartEvent

    /**
     * Event triggered when the current selection of items is cleared.
     */
    data object ClearSelection : ChecklistStartEvent
    /**
     * Event triggered when selected items are moved to a new category.
     * @property newCategory The category to which selected items are moved.
     */
    data class MoveSelectedItems(val newCategory: ItemCategory) : ChecklistStartEvent
    /**
     * Event triggered when selected items are deleted.
     * @property alsoDeleteGroceryItems Whether to also delete associated grocery items.
     */
    data class DeleteSelectedItems(val alsoDeleteGroceryItems: Boolean) : ChecklistStartEvent
    /**
     * Event triggered when changing the category of specific items.
     * @property items The list of item IDs whose category is being changed.
     * @property newCategory The new category for the items.
     */
    data class ChangeCategory(val items: List<Long>, val newCategory: ItemCategory) : ChecklistStartEvent

    /**
     * Event triggered to toggle the visibility of the drawer.
     */
    data object ToggleDrawer : ChecklistStartEvent
    /**
     * Event triggered to toggle the visibility of the delete confirmation dialog.
     */
    data object ToggleDeleteDialog : ChecklistStartEvent
    /**
     * Event triggered to toggle the visibility of the checkout dialog/UI.
     */
    data object ToggleCheckout : ChecklistStartEvent
    /**
     * Event triggered when an item's check state (completed/not completed) is toggled.
     * @property checklistItem The data of the checklist item whose check state is toggled.
     */
    data class ToggleItemCheck(val checklistItem: ChecklistItemData) : ChecklistStartEvent
    /**
     * Event triggered to toggle the visibility of the action menu for a specific checklist item.
     * @property checklist The data of the checklist item for which the action menu is toggled.
     */
    data class ToggleActionMenu(val checklist: ChecklistItemData) : ChecklistStartEvent
    /**
     * Event triggered to toggle the visibility of the change category dialog.
     */
    data object ToggleChangeCategoryDialog : ChecklistStartEvent
    /**
     * Event triggered to toggle the visibility of the copy sheet.
     */
    data object ToggleCopyDialog : ChecklistStartEvent

    data object ToggleIconPicker : ChecklistStartEvent

    /**
     * Event triggered to toggle the visibility of the more actions menu.
     */
    data object ToggleMoreActionsMenu : ChecklistStartEvent

    /**
     * Event triggered to change the item selection mode (e.g., single-select, multi-select).
     * @property mode The new item selection mode.
     */
    data class ChangeItemSelectMode(val mode: Boolean) : ChecklistStartEvent
    /**
     * Event triggered to copy selected items to a new checklist.
     * @property checklistInput Input data for creating the new checklist.
     */
    data class CopyToNewChecklist(val checklistInput: ChecklistInput) : ChecklistStartEvent
    /**
     * Event triggered to proceed with the checkout process.
     */
    data object ProceedCheckout : ChecklistStartEvent

    // Filter & Sort
    /**
     * Event triggered to toggle the visibility of the filter bottom sheet.
     */
    object ToggleFilterBottomSheet : ChecklistStartEvent
    /**
     * Event triggered to change the sorting option for checklist items.
     * @property option The new sorting option.
     */
    data class ChangeSortOption(val option: ChecklistItemOrder) : ChecklistStartEvent

    /**
     * Event triggered to toggle the selection state of a category in the filter.
     * @property category The category whose selection state is toggled.
     */
    data class ToggleCategorySelection(val category: ItemCategory) : ChecklistStartEvent
    /**
     * Event triggered to toggle the sorting direction (ascending/descending).
     */
    object ToggleSortDirection : ChecklistStartEvent
    /**
     * Event triggered to clear all applied filters.
     */
    object ClearAllFilters : ChecklistStartEvent

    // Filter Changes
    /**
     * Event triggered when a filter of a certain type is selected.
     * @property type The type of filter that is selected.
     */
    data class FilterSelection(val type: FilterType): ChecklistStartEvent
    /**
     * Event triggered when the search query changes.
     * @property query The current search query string.
     */
    data class SearchQueryEvent(val query: String) : ChecklistStartEvent

    // Item CRUD
    /**
     * Event triggered to add a new checklist item.
     * @property input The input data for the new checklist item.
     */
    data class ItemAddition(val input: ChecklistItemFormInputs) : ChecklistStartEvent
    /**
     * Event triggered to modify an existing checklist item.
     * @property checklistItemId The ID of the checklist item being modified.
     * @property input The updated input data for the checklist item.
     */
    data class ItemModification(val checklistItemId: Long, val input: ChecklistItemFormInputs) : ChecklistStartEvent
    /**
     * Event triggered to delete a checklist item.
     * @property checklistItemId The ID of the checklist item being deleted.
     * @property groceryItemId The ID of the associated grocery item, if any.
     */
    data class ItemDeletion(val checklistItemId: Long, val groceryItemId: Long?) : ChecklistStartEvent

    /**
     * Event triggered to set the new checklist being created.
     * @property checklist The input data for the new checklist.
     */
    data class SetNewChecklist(val checklist: ChecklistInput) : ChecklistStartEvent
    /**
     * Event triggered to set the checklist item being edited.
     * @property item The data of the checklist item being edited, or null if no item is being edited.
     */
    data class SetEditingItem(val item: ChecklistItemData?) : ChecklistStartEvent
    /**
     * Event triggered to load the checklist data.
     */
    data object LoadData : ChecklistStartEvent
}