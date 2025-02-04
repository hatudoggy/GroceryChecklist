package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Represents a single item in the checklist.
 *
 * @property name The name of the item.
 * @property category The category of the item (e.g., Meat, Vegetable).
 * @property price The price per unit of the item.
 * @property quantity The quantity of the item.
 * @property measurement The unit of measurement for the quantity (e.g., KILOGRAM).
 * @property isChecked Whether the item has been checked off the list.
 */
data class ChecklistData(
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Double,
    val measurement: Measurement,
    var isChecked: Boolean = false
)

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
    private val navigator: Navigator
): ViewModel() {
        private val _dummyData = MutableStateFlow(listOf(
        ChecklistData("Tender Juicy Hot dog", ItemCategory.MEAT, 250.00, 5.0, Measurement.KILOGRAM),
        ChecklistData("Chicken Breast", ItemCategory.MEAT, 350.00, 1.5, Measurement.KILOGRAM),
        ChecklistData("Ground Beef", ItemCategory.MEAT, 400.00, 2.0, Measurement.KILOGRAM),
        ChecklistData("Pork Chop", ItemCategory.MEAT, 300.00, 1.0, Measurement.KILOGRAM),
        ChecklistData("Salmon Fillet", ItemCategory.MEAT, 600.00, 0.5, Measurement.KILOGRAM),
        ChecklistData("Broccoli", ItemCategory.VEGETABLE, 120.00, 1.0, Measurement.KILOGRAM),
        ChecklistData("Carrots", ItemCategory.VEGETABLE, 80.00, 1.0, Measurement.KILOGRAM),
    ))

    // Expose dummyData as a read-only StateFlow
    private val dummyData: StateFlow<List<ChecklistData>> = _dummyData.asStateFlow()

    // MutableStateFlow to track the current filter type
    private val _filterType = MutableStateFlow(FilterType.ALL)
    // Expose filterType as a read-only StateFlow
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    // Combine dummyData and filterType to produce a filtered list of items
    val filteredItems = combine(
        dummyData,
        //Observe filterType changes
        filterType
    ) { allItems, filter ->
        when (filter) {
            FilterType.ALL -> allItems
            FilterType.CHECKED -> allItems.filter { it.isChecked }
            FilterType.UNCHECKED -> allItems.filter { !it.isChecked }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Calculate the total price of the checked items
    val checkedTotalPrice: StateFlow<Double> = filteredItems.combine(dummyData) { filtered, _ ->
        filtered.filter { it.isChecked }.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    /**
     * Mark an item as checked.
     */
    fun checkSelectedItem(item: ChecklistData) {
        _dummyData.update { list ->
            list.map {
                if (it == item) {
                    it.copy(isChecked = true)
                } else {
                    it
                }
            }
        }
    }

    /**
     * Mark an item as unchecked.
     */
    fun uncheckSelectedItem(item: ChecklistData) {
        _dummyData.update { list ->
            list.map {
                if (it == item) {
                    it.copy(isChecked = false)
                } else {
                    it
                }
            }
        }
    }


    fun setFilterType(filterType: FilterType) {
        _filterType.value = filterType
    }



    fun onEvent(event: ChecklistStartEvent) {
        when (event) {
            ChecklistStartEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}