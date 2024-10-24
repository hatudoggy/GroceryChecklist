package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ChecklistEditViewModel(
    private val navigator: Navigator
): ViewModel() {

    private val _state = MutableStateFlow(ChecklistEditState())
    val state: StateFlow<ChecklistEditState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ChecklistEditState()
    )


    fun onEvent(event: ChecklistEditEvent) {
        when (event) {
            ChecklistEditEvent.NavigateBack -> { navigator.popBackStack() }
            ChecklistEditEvent.CloseDialog -> { _state.update { it.copy(isAddingChecklistItem = false) } }
            ChecklistEditEvent.OpenDialog -> { _state.update { it.copy(isAddingChecklistItem = true) } }
            is ChecklistEditEvent.SetItemName -> { _state.update { it.copy(itemName = event.name) }}
            is ChecklistEditEvent.SetItemCategory -> { _state.update { it.copy(itemCategory = event.category) }}
            is ChecklistEditEvent.SetItemPrice -> { _state.update { it.copy(itemPrice = event.price) }}
            is ChecklistEditEvent.SetItemQuantity -> { _state.update { it.copy(itemQuantity = event.quantity) }}
        }
    }
}