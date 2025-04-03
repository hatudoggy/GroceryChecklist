package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.delay
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

    private val _state = MutableStateFlow(HistoryMainState(isLoading = true))
    val state: StateFlow<HistoryMainState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HistoryMainState(isLoading = true)
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
                navigator.navigate(Routes.HistoryDetail (event.historyId, event.checklistName))
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
            _state.update {
                it.copy(isLoading = true)
            }

            try {
                // Added a small delay to ensure loading indicator is visible
                delay(300)

                historyRepository.getAggregatedHistory()
                    .collect { unsortedCards ->
                        val sortedCards = sortHistoryData(unsortedCards)
                        _state.update {
                            it.copy(
                                cards = sortedCards,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error loading history"
                    )
                }
            }
        }
    }
}