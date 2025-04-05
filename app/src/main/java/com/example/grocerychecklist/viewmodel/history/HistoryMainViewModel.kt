package com.example.grocerychecklist.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class HistoryMainViewModel(
    private val navigator: Navigator,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryMainState())
    val state: StateFlow<HistoryMainState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HistoryMainState()
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
                navigator.navigate(HistoryDetail (event.historyId, event.checklistName))
            }

            is HistoryMainEvent.LoadHistory -> loadHistoryData()
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
                _state.update {
                    it.copy(
                        monthsList = it.monthsList + month
                    )
                }
            }
        }

        return sortedHistoryData
    }

    @OptIn(FlowPreview::class)
    private fun loadHistoryData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            try {
                // Added a small delay to ensure loading indicator is visible
                delay(300)

                val aggregatedHistory = historyRepository.getAggregatedHistory().timeout(10_000.milliseconds).firstOrNull()

                if (aggregatedHistory == null || aggregatedHistory.isEmpty()) {
                    _state.update {
                        it.copy(
                            cards = emptyList(),
                            monthsList = emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }

                    return@launch
                }

                aggregatedHistory.let { unsortedCards ->
                    val months = unsortedCards
                        .map { DateUtility.formatDate(it.history.createdAt) }
                        .distinct()
                        .sortedDescending()

                    val sortedCards = sortHistoryData(unsortedCards)
                    _state.update {
                        it.copy(
                            cards = sortedCards,
                            monthsList = months,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load history"
                    )
                }
            }
        }
    }
}