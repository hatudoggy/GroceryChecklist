package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import android.R.attr.name
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Map.entry

/**
 * ViewModel for the History Detail screen, responsible for managing and providing data
 * related to historical grocery items.
 */
class HistoryDetailViewModel(
    private val historyId: Long,
    private val checklistName: String,
    private val navigator: Navigator,
    private val historyRepository: HistoryItemRepository,
) : ViewModel() {
    private val _state: MutableStateFlow<HistoryDetailState> = MutableStateFlow(HistoryDetailState())
    val state: StateFlow<HistoryDetailState> = _state.asStateFlow()

    init {
        _state.update { it.copy(checklistName = checklistName) }
        loadHistoryItems()
        loadHistoryDetails()
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

    private fun loadHistoryDetails() {
        viewModelScope.launch {
            val history = historyRepository.getHistoryItems(historyId, ChecklistItemOrder.Name).first().first()
            _state.update { currentState ->
                currentState.copy(
                    date = DateUtility.formatDateWithDay(history.createdAt)
                )
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