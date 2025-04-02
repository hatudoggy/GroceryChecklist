package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.viewmodel.util.SearchableViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable


class ChecklistViewViewModel(
    private val checklistId: Long,
    private val navigator: Navigator,
    private val repo: ChecklistItemRepository,
): SearchableViewModel<ChecklistData>(
    matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}
){
    val uiState: StateFlow<ChecklistViewUIState> = checklistViewUIState(
        checklistId = checklistId,
        cItemRepository = repo
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistViewUIState.Loading
    )

    private val _state = MutableStateFlow(ChecklistViewState())
    val state: StateFlow<ChecklistViewState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistViewState()
    )

    private fun checklistViewUIState(
        checklistId: Long,
        cItemRepository: ChecklistItemRepository,
    ): Flow<ChecklistViewUIState> {
        val items = cItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Name)
        setItems(checklistDataMapper(items))

        return items
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        ChecklistViewUIState.Success(
                            checklistDataMapper(result.data)
                        )
                    }

                    is Result.Loading -> ChecklistViewUIState.Loading
                    is Result.Error -> ChecklistViewUIState.Error
                }
            }
    }

    fun onEvent(event: ChecklistViewEvent) {
        when (event) {
            ChecklistViewEvent.NavigateBack -> { navigator.popBackStack() }

            ChecklistViewEvent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
                _state.update { it.copy(isActionMenuOpen = false) }
            }
            ChecklistViewEvent.CloseDrawer-> {
                _state.update { it.copy(isDrawerOpen = false) }
            }

            ChecklistViewEvent.ClearSelectedItem -> { _state.update { it.copy(selectedItem = null) }}

            is ChecklistViewEvent.OpenActionMenu -> {
                _state.update { it.copy(selectedItem = event.item) }
                _state.update { it.copy(isActionMenuOpen = true) }
            }
            ChecklistViewEvent.CloseActionMenu -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                onEvent(ChecklistViewEvent.ClearSelectedItem)
            }

            ChecklistViewEvent.OpenDeleteDialog -> {
                _state.update { it.copy(isDeleteDialogOpen = true) }
            }
            ChecklistViewEvent.CloseDeleteDialog -> {
                _state.update { it.copy(isActionMenuOpen = false) }
                _state.update { it.copy(isDeleteDialogOpen = false) }
                onEvent(ChecklistViewEvent.ClearSelectedItem)
            }

            is ChecklistViewEvent.SetSearchQuery -> onSearchQueryChanged(event.query)
            is ChecklistViewEvent.AddChecklistItem -> {
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
                        onEvent(ChecklistViewEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
            is ChecklistViewEvent.EditChecklistItem -> {
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
                        onEvent(ChecklistViewEvent.CloseDrawer)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
            is ChecklistViewEvent.DeleteChecklistItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItem(
                            event.checklistId,
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistViewEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }

            is ChecklistViewEvent.DeleteChecklistItemAndItem -> {
                viewModelScope.launch {
                    try {
                        val id = repo.deleteChecklistItemAndItem(
                            event.checklistId,
                            event.itemId
                        )
                        println("Deleted Checklist Id: $id")
                        onEvent(ChecklistViewEvent.CloseDeleteDialog)
                    } catch (err: Error) {
                        Log.e("ChecklistMainViewModel", "Error adding item: ${err.message}")
                    }
                }
            }
        }
    }
}

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}