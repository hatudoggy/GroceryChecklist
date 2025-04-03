package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.StateFlow
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.ui.screen.Routes.ChecklistStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChecklistDetailViewModel(
    private val checklistId: Long,
    private val checklistRepository: ChecklistRepository,
    private val navigator: Navigator,
): ViewModel() {

    val _state = MutableStateFlow<ChecklistDetailState>(ChecklistDetailState.Loading)
    val state: StateFlow<ChecklistDetailState> = _state.asStateFlow()

    private fun loadChecklist(){
        viewModelScope.launch {
            checklistRepository.getChecklistWithDetails(checklistId)
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data != null) {
                                _state.value = ChecklistDetailState.Success(result.data)
                            } else {
                                _state.value = ChecklistDetailState.Error("Checklist not found")
                                Log.e("ChecklistDetailViewModel", "Checklist not found")
                            }
                        }
                        is Result.Loading -> _state.value = ChecklistDetailState.Loading
                        is Result.Error -> {
                            _state.value = ChecklistDetailState.Error("Failed to load checklist")
                            Log.e("ChecklistDetailViewModel", "Failed to load checklist", result.exception)
                        }
                    }
                }
        }
    }

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            is ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            is ChecklistDetailEvent.LoadData -> loadChecklist()
            is ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(ChecklistStart(event.checklistId, event.checklistName))}
        }
    }
}