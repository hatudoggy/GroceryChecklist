package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.viewmodel.SearchableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Enum class representing the different types of filters that can be applied to the checklist.
 */
enum class FilterType {
    ALL, CHECKED, UNCHECKED
}

/**
 * ViewModel for the Checklist Start screen.
 */
class ChecklistStartViewModel(
    private val navigator: Navigator,
    entry: NavBackStackEntry
): SearchableViewModel<ChecklistData>(
    matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}
) {

    init {
        setItems(
            listOf(
                ChecklistData("Chicken Breast", ItemCategory.MEAT, 350.00, 1.5, Measurement.KILOGRAM),
                ChecklistData("Ground Beef", ItemCategory.MEAT, 400.00, 2.0, Measurement.KILOGRAM),
                ChecklistData("Pork Chop", ItemCategory.MEAT, 300.00, 1.0, Measurement.KILOGRAM),
                ChecklistData("Salmon Fillet", ItemCategory.MEAT, 600.00, 0.5, Measurement.KILOGRAM),
                ChecklistData("Broccoli", ItemCategory.VEGETABLE, 120.00, 1.0, Measurement.KILOGRAM),
                ChecklistData("Carrots", ItemCategory.VEGETABLE, 80.00, 1.0, Measurement.KILOGRAM),
            )
        )
    }

    private val _state = MutableStateFlow(ChecklistStartState())
    val state: StateFlow<ChecklistStartState> = combine(
        _state, allItems, filteredItems, searchQuery
    ) { currentState, items, filteredItems, query ->
        currentState.copy(
            items = items,
            filteredItems = filterItemsChecked(currentState.selectedChip, filteredItems),
            searchQuery = query,
            totalPrice = computeTotalPrice(items)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ChecklistStartState()
    )


    fun onEvent(event: ChecklistStartEvent) {
        when (event) {
            ChecklistStartEvent.NavigateBack -> { navigator.popBackStack() }
            is ChecklistStartEvent.SelectChip -> { _state.update { it.copy(selectedChip = event.type) } }
            is ChecklistStartEvent.ToggleItemCheck -> {
                updateItems { list ->
                    list.map { item ->
                        if (item == event.item) item.copy(isChecked =  !item.isChecked) else item
                    }
                }
            }
        }
    }

    private fun filterItemsChecked(filter: FilterType, items: List<ChecklistData>): List<ChecklistData> {
        return when (filter) {
            FilterType.ALL -> items
            FilterType.CHECKED -> items.filter { it.isChecked }
            FilterType.UNCHECKED -> items.filter { !it.isChecked }
        }
    }

    private fun computeTotalPrice(items: List<ChecklistData>): Double {
        return items.filter { it.isChecked }
            .sumOf { it.price.times(it.quantity) }
    }
}


