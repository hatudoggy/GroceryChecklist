package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChecklistMainViewModel(
    private val navigator: Navigator
) : ViewModel() {

    private val _dialogState = MutableStateFlow(ChecklistMainState())
    val dialogState = _dialogState.asStateFlow()

    fun closeDialog() {
        _dialogState.update { it.copy(isAddingChecklist = false) }
    }

    fun openDialog() {
        _dialogState.update { it.copy(isAddingChecklist = true) }
    }

    fun updateChecklistName(name: String) {
        _dialogState.update { it.copy(checklistName = name) }
    }

    fun updateChecklistDescription(description: String) {
        _dialogState.update { it.copy(checklistDescription = description) }
    }

    fun onEvent(event: ChecklistMainEvent) {
        when (event) {
            ChecklistMainEvent.NavigateChecklist -> {
                navigator.navigate(Routes.ChecklistDetail)
            }
        }
    }
}