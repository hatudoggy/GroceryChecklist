package com.example.grocerychecklist.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardMainViewModel(
    private val checklistRepository: ChecklistRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _checklists = checklistRepository.getChecklists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(DashboardMainState())
    val state: StateFlow<DashboardMainState> = combine(_state, _checklists) { state, checklists ->
        state.copy(
            checklists = checklists
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        DashboardMainState()
    )

    fun onEvent(event: DashboardMainEvent) {
        when(event) {
            DashboardMainEvent.AddChecklist -> {
                val checklistInput = ChecklistInput(
                    name = "New Checklist!",
                    description = "Trip lang",
                    icon = IconOption.Home,
                    iconColor = ColorOption.White
                )
                viewModelScope.launch {
                    checklistRepository.addChecklist(checklistInput)
                }
            }

            DashboardMainEvent.ViewMoreBtn -> {
                navigator.navigate(Routes.DashboardBreakdown)
            }
        }
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                DashboardMainViewModel(GroceryChecklistApp.appModule.checklistRepository)
//            }
//        }
//    }
}

