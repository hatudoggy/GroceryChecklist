package com.example.grocerychecklist.viewmodel.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardMainViewModel(
    private val historyRepo: HistoryRepository,
    private val historyItemRepo: HistoryItemRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _state = MutableStateFlow(DashboardMainState())
    val state: StateFlow<DashboardMainState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardMainState()
    )

    init {
        viewModelScope.launch {
            launch {
                historyRepo.getHistories(4)
                    .collectLatest { histories ->
                        Log.d("DashboardMainViewModel", "histories $histories")
                        _state.update { it.copy(histories = histories) }
                    }
            }
            launch {
                historyItemRepo.getTotalItemPriceOnMonth(LocalDate.now().month)
                    .collectLatest { totalPrice ->
                        Log.d("DashboardMainViewModel", "totalPrice $totalPrice")
                        _state.update { it.copy(monthTotalPrice = totalPrice ?: 0.00) }
                    }
            }
            launch {
                historyItemRepo.getCategoryDistributionOnMonth(LocalDate.now().month)
                    .collectLatest { categories ->
                        Log.d("DashboardMainViewModel", "categories $categories")
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

