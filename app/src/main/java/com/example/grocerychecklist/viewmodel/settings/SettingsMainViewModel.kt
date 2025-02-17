package com.example.grocerychecklist.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SettingsMainViewModel(
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsMainState())
    val state: StateFlow<SettingsMainState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), SettingsMainState()
    )

    fun onEvent(event: SettingsMainEvent) {
        when (event) {
            SettingsMainEvent.ToggleBottomModal -> {
                _state.update { it.copy(isBottomModalOpen = !it.isBottomModalOpen) }
            }
        }
    }
}