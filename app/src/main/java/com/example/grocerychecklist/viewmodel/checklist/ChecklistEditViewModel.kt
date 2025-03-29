package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.util.SearchableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChecklistEditViewModel(
    private val navigator: Navigator,
    entry: NavBackStackEntry,
    private val checklistRepository: ChecklistRepository,
    private val repo: ChecklistItemRepository,
): SearchableViewModel<ChecklistData>(
    matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}
){
    val checklistId = entry.toRoute<Routes.ChecklistEdit>().checklistId

    private val _state = MutableStateFlow(ChecklistEditState())
    val state: StateFlow<ChecklistEditState> = combine(
        _state, filteredItems, searchQuery
    ) { currentState, items, query ->
        currentState.copy(items = items, searchQuery = query)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistEditState()
    )

    init {
        viewModelScope.launch {
            launch {
                _state.update { it.copy(checklistName = checklistRepository.getChecklist(checklistId).name) }
            }

            launch {
                repo.getChecklistItems(checklistId, ChecklistItemOrder.Name)
                    .collect { setItems(checklistDataMapper(it)) }
            }
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
                            event.checklistId,
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistEditEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }

            is ChecklistEditEvent.DeleteChecklistItemAndItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItemAndItem(
                            event.checklistId,
                            event.itemId
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