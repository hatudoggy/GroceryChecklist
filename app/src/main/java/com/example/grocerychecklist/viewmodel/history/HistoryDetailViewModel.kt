package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the History Detail screen, responsible for managing and providing data
 * related to historical grocery items.
 */
class HistoryDetailViewModel(
    private val navigator: Navigator,
    entry: NavBackStackEntry,
    private val historyRepository: HistoryItemRepository
) : ViewModel() {
      private val historyId = entry.toRoute<Routes.HistoryDetail>().historyId
    
    private val _state = MutableStateFlow(HistoryDetailState())
    val state: StateFlow<HistoryDetailState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), HistoryDetailState()
    )

    init {
        loadHistoryItems()
    }

    private fun loadHistoryItems() {
        viewModelScope.launch {
            historyRepository.getHistoryItems(historyId, ChecklistItemOrder.Name)
                .collect { items ->
                    val historyDataDetails = items.map {
                        HistoryDataDetails(
                            name = it.name,
                            category = ItemCategory.valueOf(it.category),
                            price = it.price,
                            quantity = it.quantity.toDouble(),
                            measurement = Measurement.valueOf(it.measureType)
                        )
                    }
                    setHistoryItems(historyDataDetails)
                }
        }
    }

    private fun updateFilteredItems() {
        _state.update { currentState ->
            currentState.copy(
                filteredItems = if (currentState.selectedCategories.contains(ItemCategory.ALL)) {
                    currentState.historyItems
                } else {
                    currentState.historyItems.filter { it.category in currentState.selectedCategories }
                }
            )
        }
    }

    private fun updateSelectedCategories(category: ItemCategory) {
        _state.update { currentState ->
            val newSelectedCategories = when {
                category == ItemCategory.ALL -> setOf(ItemCategory.ALL)
                currentState.selectedCategories.contains(ItemCategory.ALL) -> setOf(category)
                currentState.selectedCategories.contains(category) -> currentState.selectedCategories - category
                else -> currentState.selectedCategories + category
            }
            currentState.copy(selectedCategories = newSelectedCategories)
        }
        updateFilteredItems()
    }

    /**
     * Sets the list of history items.
     */
    private fun setHistoryItems(items: List<HistoryDataDetails>) {
        _state.value = _state.value.copy(
            historyItems = items
        )
        updateFilteredItems()
    }

    /**
     * Handles events from the UI.
     * @param event The event to handle.
     */
    fun onEvent(event: HistoryDetailEvent) {
        when (event) {
            HistoryDetailEvent.NavigateBack -> { navigator.popBackStack() }
            is HistoryDetailEvent.SelectCategory -> updateSelectedCategories(event.category)
        }
    }
}