package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.SearchableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    entry: NavBackStackEntry,
    private val repo: ChecklistItemRepository,
    private val checklistRepo: ChecklistRepository,
    private val historyRepo: HistoryRepository,
    private val historyItemRepo: HistoryItemRepository,
): SearchableViewModel<ChecklistData>(
    matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}
) {
    val checklistId = entry.toRoute<Routes.ChecklistStart>().checklistId

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

    init {
        viewModelScope.launch {
            repo.getChecklistItems(checklistId, ChecklistItemOrder.Name)
                .collect { setItems(checklistDataMapper(it)) }
        }
    }


    fun onEvent(event: ChecklistStartEvent) {
        when (event) {
            ChecklistStartEvent.NavigateBack -> { navigator.popBackStack() }

            is ChecklistStartEvent.SelectChip -> { _state.update { it.copy(selectedChip = event.type) } }
            is ChecklistStartEvent.ToggleItemCheck -> {
                _state.update { currentState ->
                    val updatedList = currentState.checkedItems.toMutableList()
                    if (isChecked(event.item)) {
                        updatedList.removeAll { it.id == event.item.id }
                    } else {
                        updatedList.add(event.item)  // Add item
                    }
                    currentState.copy(checkedItems = updatedList)
                }
            }

            ChecklistStartEvent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
                _state.update { it.copy(isActionMenuOpen = false) }
            }

            ChecklistStartEvent.CloseDrawer -> { _state.update { it.copy(isDrawerOpen = false) } }

            is ChecklistStartEvent.OpenActionMenu -> {
                _state.update { it.copy(selectedItem = event.item) }
                _state.update { it.copy(isActionMenuOpen = true) }
            }

            ChecklistStartEvent.CloseActionMenu -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                onEvent(ChecklistStartEvent.ClearSelectedItem)
            }

            ChecklistStartEvent.OpenDeleteDialog -> {
                _state.update { it.copy(isDeleteDialogOpen = true) }
            }

            ChecklistStartEvent.CloseDeleteDialog -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                _state.update { it.copy(isDeleteDialogOpen = false) }
                onEvent(ChecklistStartEvent.ClearSelectedItem)
            }

            ChecklistStartEvent.OpenCheckout -> { _state.update { it.copy(isCheckoutOpen = true) } }

            ChecklistStartEvent.CloseCheckout -> { _state.update { it.copy(isCheckoutOpen = false) } }

            is ChecklistStartEvent.ProceedCheckout -> {
                viewModelScope.launch {
                    try {
                        val checklist = checklistRepo.getChecklist(checklistId)
                        val historyId = historyRepo.addHistory(checklist)
                        val mapChecked = event.items.map {
                            it.copy(isChecked = isChecked(it))
                        }
                        historyItemRepo.addHistoryItems(
                            historyId, filterItemsChecked(FilterType.CHECKED, mapChecked)
                        )

                        onEvent(ChecklistStartEvent.CloseCheckout)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error checking out: ${err.message}")
                    }
                }
            }

            ChecklistStartEvent.ClearSelectedItem -> { _state.update { it.copy(selectedItem = null) } }


            is ChecklistStartEvent.AddChecklistItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.addChecklistItem(
                            checklistId,
                            checklistItemInput = ChecklistItemInput(
                                name = event.formInputs.name,
                                price = event.formInputs.price,
                                quantity = event.formInputs.quantity,
                                category = event.formInputs.category.name,
                                measureType = "",
                                measureValue = 0.00,
                                photoRef = ""
                            )
                        )
                        println("Created Checklist Id: $id")
                        onEvent(ChecklistStartEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
            is ChecklistStartEvent.EditChecklistItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.updateChecklistItem(
                            event.checklistId,
                            checklistItemInput = ChecklistItemInput(
                                name = event.formInputs.name,
                                price = event.formInputs.price,
                                quantity = event.formInputs.quantity,
                                category = event.formInputs.category.name,
                                measureType = "",
                                measureValue = 0.00,
                                photoRef = ""
                            )
                        )
                        println("Edited Checklist Id: $id")
                        onEvent(ChecklistStartEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error updating item: ${err.message}")
                    }
                }
            }
            is ChecklistStartEvent.DeleteChecklistItem -> {
                val item = _state.value.filteredItems.find { it.id == event.checklistId }
                if (item != null) onEvent(ChecklistStartEvent.ToggleItemCheck(item))
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItem(
                            event.checklistId,
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistStartEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error deleting item: ${err.message}")
                    }
                }
            }
            is ChecklistStartEvent.DeleteChecklistItemAndItem -> {
                val item = _state.value.filteredItems.find { it.id == event.itemId }
                if (item != null) onEvent(ChecklistStartEvent.ToggleItemCheck(item))
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItemAndItem(
                            event.checklistId,
                            event.itemId
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistStartEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error deleting item: ${err.message}")
                    }
                }
            }
        }
    }

    private fun filterItemsChecked(filter: FilterType, items: List<ChecklistData>): List<ChecklistData> {
        return when (filter) {
            FilterType.ALL -> items
            FilterType.CHECKED -> items.filter { isChecked(it) }
            FilterType.UNCHECKED -> items.filter { !isChecked(it) }
        }
    }

    private fun computeTotalPrice(items: List<ChecklistData>): Double {
        return items.filter { isChecked(it) }
            .sumOf { it.price.times(it.quantity) }
    }

    fun isChecked(item: ChecklistData): Boolean {
        return _state.value.checkedItems.any { it.id == item.id }
    }
}


