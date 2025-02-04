package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the History Detail screen, responsible for managing and providing data
 * related to historical grocery items.
 */
class HistoryDetailViewModel(
    private val navigator: Navigator
) : ViewModel() {
    // Dummy data for initial state of the history items
    private val dummyItems = listOf(
        HistoryDataDetails("Eggs", ItemCategory.POULTRY, 120.0, 1.00, Measurement.DOZEN),
        HistoryDataDetails("Tomatoes", ItemCategory.VEGETABLE, 20.0, 1.00, Measurement.KILOGRAM),
        HistoryDataDetails("Pork", ItemCategory.MEAT, 150.0, 1.00, Measurement.KILOGRAM),
        HistoryDataDetails("Rice", ItemCategory.GRAIN, 30.0, 1.00, Measurement.KILOGRAM),
        HistoryDataDetails("Ice Cream", ItemCategory.DAIRY, 100.0, 1.00, Measurement.TUB)
    )

    // Mutable StateFlow to hold the list of history items
    private val _historyItems = MutableStateFlow(dummyItems)

    // Mutable StateFlow to hold the currently selected item categories for filtering
    private val _selectedCategories = MutableStateFlow(setOf(ItemCategory.ALL))
    // Exposed StateFlow to observe the selected categories
    val selectedCategories: StateFlow<Set<ItemCategory>> = _selectedCategories

    /**
     * A StateFlow that combines the history items and selected categories to provide a filtered list of items.
     * If 'ALL' is selected, all items are shown; otherwise, items are filtered by the selected categories.
     */
    val filteredItems = combine(_historyItems, _selectedCategories) { items, selectedCategories ->
        if (selectedCategories.contains(ItemCategory.ALL)) {
            items
        } else {
            items.filter { it.category in selectedCategories }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Updates the set of selected categories. If 'ALL' is selected, it's the only category.
     * If 'ALL' is already selected, it replaces it with the new category. Otherwise, toggles the category on/off.
     */
    fun updateSelectedCategories(category: ItemCategory) {
        _selectedCategories.value = when {
            category == ItemCategory.ALL -> setOf(ItemCategory.ALL)
            _selectedCategories.value.contains(ItemCategory.ALL) -> setOf(category)
            _selectedCategories.value.contains(category) -> _selectedCategories.value - category
            else -> _selectedCategories.value + category
        }
    }

    /**
     * Sets the list of history items.
     */
    private fun setHistoryItems(items: List<HistoryDataDetails>) {
        _historyItems.value = items
    }

    /**
     * Handles events from the UI.
     * @param event The event to handle.
     */
    fun onEvent(event: HistoryDetailEvent) {
        when (event) {
            HistoryDetailEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }

    init {
        setHistoryItems(dummyItems)
    }
}