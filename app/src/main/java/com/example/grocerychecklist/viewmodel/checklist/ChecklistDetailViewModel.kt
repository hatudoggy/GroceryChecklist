package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.repository.ChecklistDetails
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.StateFlow
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.ui.screen.Routes.ChecklistStart
import com.example.grocerychecklist.ui.screen.checklist.CategorySummary
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChecklistDetailViewModel(
    private val checklistId: Long,
    private val checklistRepository: ChecklistRepository,
    private val navigator: Navigator,
): ViewModel() {

    val _state = MutableStateFlow<ChecklistDetailUIState>(ChecklistDetailUIState.Loading)
    val state: StateFlow<ChecklistDetailUIState> = _state.asStateFlow()

    init {
        loadChecklist()
    }

    private fun loadChecklist(){
        viewModelScope.launch {
            checklistRepository.getChecklistWithDetails(checklistId)
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data != null) {
                                _state.value = ChecklistDetailUIState.Success(result.data)
                            } else {
                                _state.value = ChecklistDetailUIState.Error("Checklist not found")
                                Log.e(TAG, "Checklist not found")
                            }
                        }
                        is Result.Loading -> _state.value = ChecklistDetailUIState.Loading
                        is Result.Error -> {
                            _state.value = ChecklistDetailUIState.Error("Failed to load checklist")
                            Log.e(TAG, "Failed to load checklist", result.exception)
                        }
                    }
                }
        }
    }

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            is ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            is ChecklistDetailEvent.LoadData -> loadChecklist()
            is ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(ChecklistStart(event.checklistId, event.checklistName, ChecklistMode.SHOPPING, null))}
            is ChecklistDetailEvent.NavigateViewMode -> {navigator.navigate(ChecklistStart(event.checklistId, event.checklistName, ChecklistMode.EDIT, event.itemCategory))}
        }
    }

    companion object{
        const val TAG = "ChecklistDetailVM"
    }
}