package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.ui.component.Measurement
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
import java.util.Locale.Category

class ChecklistEditViewModel(
    private val navigator: Navigator,
    entry: NavBackStackEntry,
    private val repo: ChecklistItemRepository,
): SearchableViewModel<ChecklistItemFull>(
    matchesSearch = { item, query -> item.item.name.contains(query, ignoreCase = true)}
){
    val checklistId = entry.toRoute<Routes.ChecklistEdit>().checklistId

    private val _state = MutableStateFlow(ChecklistEditState())
    val state: StateFlow<ChecklistEditState> = combine(
        _state, filteredItems, searchQuery
    ) { currentState, items, query ->
        currentState.copy(items = items, searchQuery = query)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ChecklistEditState()
    )

    init {
        viewModelScope.launch {
            repo.getChecklistItems(checklistId, ChecklistItemOrder.Name)
                .collect { setItems(it) }
        }
    }

    fun onEvent(event: ChecklistEditEvent) {
        when (event) {
            ChecklistEditEvent.NavigateBack -> { navigator.popBackStack() }

            ChecklistEditEvent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
                _state.update { it.copy(isActionMenuOpen = false) }
            }
            ChecklistEditEvent.CloseDrawer-> {
                _state.update { it.copy(isDrawerOpen = false) }
            }

//            is ChecklistEditEvent.SelectItem -> {
//                _state.update { it.copy(selectedItem = event.item) }
//            }
            ChecklistEditEvent.ClearSelectedItem -> { _state.update { it.copy(selectedItem = null) }}

            is ChecklistEditEvent.OpenActionMenu -> {
                _state.update { it.copy(selectedItem = event.item) }
                _state.update { it.copy(isActionMenuOpen = true) }
            }
            ChecklistEditEvent.CloseActionMenu -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                onEvent(ChecklistEditEvent.ClearSelectedItem)
            }

            ChecklistEditEvent.OpenDeleteDialog -> {
                _state.update { it.copy(isDeleteDialogOpen = true) }
            }
            ChecklistEditEvent.CloseDeleteDialog -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                _state.update { it.copy(isDeleteDialogOpen = false) }
                onEvent(ChecklistEditEvent.ClearSelectedItem)
            }

            is ChecklistEditEvent.SetSearchQuery -> onSearchQueryChanged(event.query)
            is ChecklistEditEvent.AddChecklistItem -> {
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
                        onEvent(ChecklistEditEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
            is ChecklistEditEvent.EditChecklistItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.updateChecklistItem(
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
                        println("Edited Checklist Id: $id")
                        onEvent(ChecklistEditEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
            is ChecklistEditEvent.DeleteChecklistItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItem(
                            event.item,
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistEditEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
        }
    }
}