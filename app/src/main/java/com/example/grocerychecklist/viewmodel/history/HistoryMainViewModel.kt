package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryMainViewModel(
    private val navigator: Navigator,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryMainState())
    val state: StateFlow<HistoryMainState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryMainState()
    )

    init {
        loadHistoryData()
    }

    fun onEvent(event: HistoryMainEvent) {
        when (event) {
            is HistoryMainEvent.ToggleCard -> {
                _state.update {
                    it.copy(cardStates = it.cardStates.toMutableMap().apply {
                        this[event.id] = this[event.id] != true
                    })
                }
            }

            is HistoryMainEvent.NavigateHistory -> {
                navigator.navigate(Routes.HistoryDetail (event.historyId))
            }
        }
    }

    private fun sortHistoryData(historyData: List<HistoryMapped>): List<HistoryMapped> {
        val sortedHistoryData = historyData.sortedByDescending {
            when (val createdAt = it.history.createdAt) {
                else -> createdAt.toLocalDate()
            }
        }

        sortedHistoryData.forEach { data ->
            val month = DateUtility.formatDate(data.history.createdAt)

            if (!_state.value.monthsList.contains(month)) {
                _state.value.monthsList.add(month)
            }
        }

        return sortedHistoryData
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            val unsortedCards =
                historyRepository.getAggregatedHistory().stateIn(viewModelScope).value
            _state.value = _state.value.copy(
                // Sort the cards by date in descending order
                cards = sortHistoryData(unsortedCards)
            )
        }
    }
}