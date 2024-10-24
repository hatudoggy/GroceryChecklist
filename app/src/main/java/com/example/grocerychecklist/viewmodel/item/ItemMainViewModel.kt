package com.example.grocerychecklist.viewmodel.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ItemMainViewModel: ViewModel() {

    private val _state = MutableStateFlow(ItemMainState())
    val state: StateFlow<ItemMainState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ItemMainState()
    )

    fun onEvent(event: ItemMainEvent) {
        when (event) {
            ItemMainEvent.OpenDialog -> { _state.update { it.copy(isAddingItem = true) }}
            ItemMainEvent.CloseDialog -> { _state.update { it.copy(isAddingItem = false) }}
        }
    }
}