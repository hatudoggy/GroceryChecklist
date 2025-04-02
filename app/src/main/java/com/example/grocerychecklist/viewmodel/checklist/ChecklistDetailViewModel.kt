package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes.ChecklistDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.ui.screen.Routes.ChecklistEdit
import com.example.grocerychecklist.ui.screen.Routes.ChecklistStart
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.timeout
import kotlin.time.Duration.Companion.seconds


class ChecklistDetailViewModel(
    private val checklistRepository: ChecklistRepository,
    private val navigator: Navigator,
    entry: NavBackStackEntry
): ViewModel() {
    val checklistId = entry.toRoute<ChecklistDetail>().checklistId
    var checklistName = ""

    val state: StateFlow<ChecklistDetailState> = checklistDetailUIState(
        checklistId = checklistId,
        checklistRepository = checklistRepository
    ).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ChecklistDetailState.Loading
    )

    @OptIn(FlowPreview::class)
    private fun checklistDetailUIState(
        checklistId: Long,
        checklistRepository: ChecklistRepository,
    ): Flow<ChecklistDetailState> {
        return checklistRepository.getChecklistWithDetails(checklistId)
            .timeout(10.seconds)
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        checklistName = result.data.name
                        ChecklistDetailState.Success(result.data)
                    }
                    is Result.Loading -> ChecklistDetailState.Loading
                    is Result.Error -> ChecklistDetailState.Error
                }
            }
    }

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            ChecklistDetailEvent.NavigateViewMode -> {navigator.navigate(ChecklistEdit(checklistId, checklistName = checklistName))}
            ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(ChecklistStart(checklistId))}
        }
    }
}