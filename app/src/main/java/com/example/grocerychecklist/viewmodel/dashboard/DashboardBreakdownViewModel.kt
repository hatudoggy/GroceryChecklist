package com.example.grocerychecklist.viewmodel.dashboard

import android.util.Log
import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month

/**
 * Data class representing a category's data for a dashboard.
 *
 * This class encapsulates the information needed to display a single category's
 * performance or status on a dashboard. It includes the category itself,
 * the percentage it represents relative to the total, and its raw value.
 *
 * @property category The [ItemCategory] that this data represents. This could be
 *                    any type of categorization, such as "Meat," "Fruit,"
 *                    or "Medicine"
 * @property percent The percentage this category represents out of the total.
 *                   This value should be between 0.0 and 100.0 (inclusive).
 * @property expenses The raw value of expenses associated with this category.
 */
data class DashboardCategoryData(
    val category: ItemCategory,
    val percent: Float,
    val expenses: Double
)

data class DashboardGraphData(
    val date: String,
    val expenses: Double = 0.0
)

class DashboardBreakdownViewModel(
    private val navigator: Navigator,
    private val historyRepo: HistoryRepository,
    private val historyItemRepo: HistoryItemRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardBreakdownState())
    val state: StateFlow<DashboardBreakdownState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardBreakdownState()
    )

    init {
        viewModelScope.launch {
            filterData("Today")
            getDashboardGraphData()
            logState()
        }
    }

    private fun logState() {
        Log.d("DashboardBreakdownVM", _state.value.toString())
    }

    private fun getDashboardGraphData() {
        viewModelScope.launch {
            try {
                val currentMonth = DateUtility.getCurrentMonth()
                val monthList = mutableListOf<Month>()

                // Get the past 3 months
                for(i in 0..2) {
                    monthList.add(DateUtility.getPreviousMonthsFromNow(currentMonth, i))
                }

                // Collect all monthly expenses first
                val monthlyExpenses = mutableMapOf<Month, Double>()

                // For each month, calculate its expenses
                monthList.forEach { month ->
                    val historyIds = mutableListOf<Long>()
                    historyRepo.getHistoriesFromMonth(month).first().forEach { history ->
                        historyIds.add(history.id)
                    }

                    val monthExpense = getTotalExpensesFromHistoryIds(historyIds)
                    monthlyExpenses[month] = monthExpense
                }

                val dashboardGraphDataList = monthlyExpenses.map { (month, expense) ->
                    DashboardGraphData(month.name, expense)
                }

                // Sort by month (most recent first) and take 3
                val sortedData = dashboardGraphDataList.sortedBy {
                    Month.valueOf(it.date.uppercase())
                }.take(3)

                Log.d("DashboardBreakdownVM", "Graph data: $sortedData")
                val maxValue = calculateDashboardGraphMaxValue(sortedData)
                Log.d("DashboardBreakdownVM", "Setting maxValue to: $maxValue")
                _state.update {
                    it.copy(dashboardGraphData = sortedData, dashboardGraphMaxValue = maxValue)
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error processing graph data", e)
                _state.update { it.copy(errorMessage = "Error loading graph data") }
            }
        }
    }

    private fun calculateDashboardGraphMaxValue(data: List<DashboardGraphData>): Double {
        val maxValue = data.maxOf { it.expenses }
        return maxValue + (maxValue * 0.25)
    }

    private fun getTodayData() {
        viewModelScope.launch {
            try {
                val histories = historyRepo.getHistoriesFromDateRange(
                    DateUtility.getStartOfDay(LocalDateTime.now()),
                    DateUtility.getEndOfDay(LocalDateTime.now())).first()
                val historyIds = histories.map { it.id }

                if (historyIds.isEmpty()) {
                    _state.update { it.copy(
                        errorMessage = "No history found for today.",
                        dashboardCategoryData = emptyList()
                    )}
                } else {
                    setData(historyIds)
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error fetching today's history data", e)
                _state.update { it.copy(
                    errorMessage = "Error fetching today's history data",
                    dashboardCategoryData = emptyList()
                )}
            }
        }
    }

    private fun getYesterdayData() {
        viewModelScope.launch {
            try {
                val yesterday = DateUtility.getYesterdayStartOfDay()
                val histories = historyRepo.getHistoriesFromDateRange(
                    yesterday,
                    DateUtility.getEndOfDay(yesterday)).first()
                val historyIds = histories.map { it.id }

                if (historyIds.isEmpty()) {
                    _state.update { it.copy(
                        errorMessage = "No history found for yesterday.",
                        dashboardCategoryData = emptyList() // Clear any previous data
                    )}
                } else {
                    setData(historyIds)
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error fetching yesterday's history data", e)
                _state.update { it.copy(
                    errorMessage = "Error fetching yesterday's history data",
                    dashboardCategoryData = emptyList() // Clear any previous data
                )}
            }
        }
    }

    private fun getThisWeekData() {
        viewModelScope.launch {
            try {
                val histories = historyRepo.getHistoriesFromDateRange(DateUtility.getThisWeekDateStartDay(), DateUtility.getEndOfDay(LocalDateTime.now())).first()
                val historyIds = histories.map { it.id }

                if (historyIds.isEmpty()){
                    _state.update { it.copy(
                        errorMessage = "No history found for this week.",
                        dashboardCategoryData = emptyList() // Clear any previous data
                    )}
                } else {
                    setData(historyIds)
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error fetching this week's history data", e)
                _state.update { it.copy(
                    errorMessage = "Error fetching this week's history data",
                    dashboardCategoryData = emptyList() // Clear any previous data
                )}
            }
        }
    }

    private fun getThisMonthData() {
        viewModelScope.launch {
            try {
                val histories = historyRepo.getHistoriesFromDateRange(DateUtility.getThisMonthStartDay(), DateUtility.getThisMonthEndDay()).first()
                val historyIds = histories.map { it.id }

                if (historyIds.isEmpty()){
                    _state.update { it.copy(
                        errorMessage = "No history found for this month.",
                        dashboardCategoryData = emptyList() // Clear any previous data
                    )}
                } else {
                    setData(historyIds) // Set data for this month
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error fetching this month's history data", e)
                _state.update { it.copy(
                    errorMessage = "Error fetching this month's history data",
                    dashboardCategoryData = emptyList() // Clear any previous data
                )}
            }
        }
    }

    private fun setData(historyIds: List<Long>) {
        if (historyIds.isEmpty()) {
            Log.d("DashboardBreakdownVM", "setData called with empty historyIds list")
            _state.update {
                it.copy(
                    dashboardCategoryData = emptyList()
                )
            }
            return // Don't proceed with empty list
        }

        viewModelScope.launch {
            try {
                Log.d("DashboardBreakdownVM", "Processing ${historyIds.size} history IDs")

                // Collect all history items first
                val allHistoryItems = mutableListOf<HistoryItem>()
                historyIds.forEach { historyId ->
                    val items = historyItemRepo.getHistoryItems(historyId, ChecklistItemOrder.Date).first()
                    Log.d("DashboardBreakdownVM", "History ID $historyId has ${items.size} items")
                    allHistoryItems.addAll(items)
                }

                if (allHistoryItems.isEmpty()) {
                    Log.d("DashboardBreakdownVM", "No history items found for the given history IDs")
                    _state.update { currentState ->
                        currentState.copy(
                            errorMessage = "No items found for the selected period.",
                            dashboardCategoryData = emptyList()
                        )
                    }
                } else {
                    // Then update state once with all data
                    val dashboardData = createDashboardData(allHistoryItems)
                    Log.d("DashboardBreakdownVM", "Created dashboard data with ${dashboardData.size} categories")
                    _state.update { currentState ->
                        currentState.copy(
                            dashboardCategoryData = dashboardData,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error setting data for history IDs", e)
                _state.update { it.copy(
                    errorMessage = "Error setting data for the selected period",
                    dashboardCategoryData = emptyList()
                )}
            }
        }
    }


    private suspend fun getTotalExpensesFromHistoryIds(historyIds: List<Long>): Double {
        var totalExpenses = 0.0
        if (historyIds.isNotEmpty()) {
            try {
                historyIds.forEach { historyId ->
                    val items = historyItemRepo.getHistoryItems(historyId, ChecklistItemOrder.Date).first()
                    totalExpenses += items.sumOf { it.price * it.quantity }
                }
            } catch (e: Exception) {
                Log.e("DashboardBreakdownVM", "Error calculating total expenses", e)
            }
        }
        return totalExpenses
    }

    private fun createDashboardData(data: List<HistoryItem>): List<DashboardCategoryData> {
        val total = calculateTotal(data)
        Log.d("DashboardBreakdownVM", "Total expenses: $total")

        return ItemCategory.entries.mapNotNull { category ->
            val expenses = calculateExpensesForCategory(data, category)
            val percentage = if (total > 0) calculatePercentage(expenses, total) else 0f
            Log.d("DashboardBreakdownVM", "Category: ${category.text}, Expenses: $expenses, Percentage: $percentage")

            if (expenses > 0) {
                DashboardCategoryData(category, percentage, expenses)
            } else {
                null
            }
        }
    }

    private fun calculatePercentage(categoryTotal: Double, total: Double): Float {
        return (categoryTotal / total * 100).toFloat()
    }

    private fun calculateExpensesForCategory(
        data: List<HistoryItem>,
        itemCategory: ItemCategory
    ): Double {
        return data.filter { it.category == itemCategory.name }
            .sumOf { it.price * it.quantity }
    }

    private fun calculateTotal(data: List<HistoryItem>): Double {
        return data.sumOf { it.price * it.quantity }
    }

    /**
     * Filters the dashboard category data based on the selected day.
     *
     * This function updates the [_dashboardCategoryData] LiveData with the appropriate
     * dummy data based on the provided `day` string. It uses a `when` expression to
     * map the `day` to the corresponding data set.
     *
     * @param day The selected day (e.g., "Today", "Yesterday", "This Week", "This Month").
     *            It should be one of the values present in the `days` list.
     *            If the provided day is not in the days list, it defaults to showing `dummyDataToday`.
     *
     * @see days
     * @see dummyDataToday
     * @see dummyDataYesterday
     * @see dummyDataThisWeek
     * @see dummyDataThisMonth
     * @see DashboardCategoryData
     * @see _dashboardCategoryData
     */
    private fun filterData(day: String) {
        _state.update {
            it.copy(selectedDay = day)
        }
        when (day) {
            "Today" -> getTodayData()
            "Yesterday" -> getYesterdayData()
            "This Week" -> getThisWeekData()
            "This Month" -> getThisMonthData()
            else -> getTodayData()
        }
    }

    /**
     * Handles events related to the Dashboard Breakdown screen.
     *
     * This function processes different events that can occur within the
     * Dashboard Breakdown feature. Currently, it handles navigation events.
     *
     * @param event The [DashboardBreakdownEvent] that has occurred.
     *
     * @see DashboardBreakdownEvent
     */
    fun onEvent(event: DashboardBreakdownEvent) {
        when (event) {
            DashboardBreakdownEvent.NavigateBack -> {
                navigator.popBackStack()
            }

            is DashboardBreakdownEvent.SelectDay -> {
                filterData(event.selectedDay)
            }
        }
    }
}