package com.example.grocerychecklist.viewmodel.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.checklist.checklistDataMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardMainViewModel(
    historyRepo: HistoryRepository,
    historyItemRepo: HistoryItemRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _state = MutableStateFlow(DashboardMainState())
    val state: StateFlow<DashboardMainState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            launch {
                historyRepo.getHistories(4)
                    .collectLatest { histories ->
                        _state.update { it.copy(histories = histories) }
                    }
            }
            launch {
                historyItemRepo.getTotalItemPriceOnMonth(LocalDate.now())
                    .collectLatest { totalPrice ->
                        _state.update { it.copy(monthTotalPrice = totalPrice ?: 0.00) }
                    }
            }
            launch {
                historyItemRepo.getCategoryDistributionOnMonth(_state.value.currentDate)
                    .collectLatest { categories ->
                        _state.update { it.copy(categoryBreakdown = processCategoryBreakdown(categories)) }
                    }
            }
        }
    }

    fun onEvent(event: DashboardMainEvent) {
        when(event) {
            DashboardMainEvent.ViewMoreBtn -> {
                navigator.navigate(Routes.DashboardBreakdown)
            }
            DashboardMainEvent.NavigateHistoryMain -> {
                navigator.navigate(Routes.HistoryMain)
            }
            is DashboardMainEvent.NavigateHistoryDetail -> {
                navigator.navigate(Routes.HistoryDetail(event.historyId))
            }
        }
    }

    private fun processCategoryBreakdown(categories: List<HistoryItemAggregated>): List<HistoryItemAggregated> {
        if (categories.isEmpty()) return emptyList()

        val topCategories = categories.take(4)
        val othersPrice = categories.drop(5).sumOf { it.sumOfPrice }
        val othersCount = categories.drop(5).sumOf { it.totalItems }

        return if (othersPrice > 0) {
            topCategories + HistoryItemAggregated(othersPrice, othersCount, "OTHER")
        } else {
            topCategories
        }
    }

}

