package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChecklistMainViewModel : ViewModel() {
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
}