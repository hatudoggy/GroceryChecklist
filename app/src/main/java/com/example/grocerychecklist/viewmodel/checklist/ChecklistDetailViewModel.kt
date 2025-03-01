package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChecklistDetailViewModel(
    private val checklistRepository: ChecklistRepository,
    private val checklistItemRepository: ChecklistItemRepository,
    private val navigator: Navigator,
    entry: NavBackStackEntry
): ViewModel() {
    val checklistId = entry.toRoute<Routes.ChecklistDetail>().checklistId
    private val _state = MutableStateFlow<ChecklistDetailState>(ChecklistDetailState.Loading)
    val state: StateFlow<ChecklistDetailState> = _state


    init {
        loadChecklistDetails()
    }

    private fun loadChecklistDetails() {
        viewModelScope.launch {
            try {
                val checklist = checklistRepository.getChecklist(checklistId)
                val totalPrice = checklistItemRepository.getTotalChecklistItemPrice(checklistId)
                val itemCount = checklistItemRepository.getTotalChecklistItems(checklistId)
                _state.value = ChecklistDetailState.Loaded(
                    checklist,
                    totalPrice,
                    itemCount
                )
            } catch (_: Exception) {
                _state.value = ChecklistDetailState.Error
            }
        }
    }

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            ChecklistDetailEvent.NavigateViewMode -> {navigator.navigate(Routes.ChecklistView(checklistId))}
            ChecklistDetailEvent.NavigateEditMode -> {navigator.navigate(Routes.ChecklistEdit(checklistId))}
            ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(Routes.ChecklistStart(checklistId))}
        }
    }
}