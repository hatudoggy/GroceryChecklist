package com.example.grocerychecklist.viewmodel.dashboard

import ItemCategory
import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
) {}

//TODO: retrieve the history data and create a logic to calculate the percentage and value
class DashboardBreakdownViewModel(
    private val navigator: Navigator
): ViewModel() {
    // Day selector
    val days = listOf("Today", "Yesterday", "This Week", "This Month")

    // Dummy data for each day
    private val dummyDataToday: List<DashboardCategoryData> = listOf(
        DashboardCategoryData(ItemCategory.MEAT, 0.33f, 6502.00),
        DashboardCategoryData(ItemCategory.FRUIT, 0.28f, 5502.00),
        DashboardCategoryData(ItemCategory.VEGETABLE, 0.23f, 4502.00),
        DashboardCategoryData(ItemCategory.MEDICINE, 0.18f, 3502.00),
    )

    private val dummyDataYesterday: List<DashboardCategoryData> = listOf(
        DashboardCategoryData(ItemCategory.POULTRY, 0.62f, 2502.00),
        DashboardCategoryData(ItemCategory.CLEANING, 0.38f, 1502.00),
    )

    private val dummyDataThisWeek: List<DashboardCategoryData> = listOf(
        DashboardCategoryData(ItemCategory.MEAT, 0.27f, 6502.00),
        DashboardCategoryData(ItemCategory.FRUIT, 0.23f, 5502.00),
        DashboardCategoryData(ItemCategory.VEGETABLE, 0.19f, 4502.00),
        DashboardCategoryData(ItemCategory.MEDICINE, 0.15f, 3502.00),
        DashboardCategoryData(ItemCategory.POULTRY, 0.1f, 2502.00),
        DashboardCategoryData(ItemCategory.CLEANING, 0.06f, 1502.00),
    )

    private val dummyDataThisMonth: List<DashboardCategoryData> = listOf(
        DashboardCategoryData(ItemCategory.MEAT, 0.22f, 6502.00),
        DashboardCategoryData(ItemCategory.FRUIT, 0.19f, 5502.00),
        DashboardCategoryData(ItemCategory.VEGETABLE, 0.16f, 4502.00),
        DashboardCategoryData(ItemCategory.MEDICINE, 0.12f, 3502.00),
        DashboardCategoryData(ItemCategory.POULTRY, 0.09f, 2502.00),
        DashboardCategoryData(ItemCategory.CLEANING, 0.05f, 1502.00),
        DashboardCategoryData(ItemCategory.DAIRY, 0.11f, 3202.00),
        DashboardCategoryData(ItemCategory.OTHER, 0.04f, 1202.00),
    )

    // StateFlow to hold the current dashboard category data
    private val _dashboardCategoryData = MutableStateFlow(dummyDataToday)
    // Publicly exposed StateFlow for observing the dashboard category data
    val dashboardCategoryData: StateFlow<List<DashboardCategoryData>> = _dashboardCategoryData

    // StateFlow to hold the currently selected day
    private val _selectedDay = MutableStateFlow(setOf(days[0]))
    // Publicly exposed StateFlow for observing the selected day
    val selectedDay: StateFlow<Set<String>> = _selectedDay


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
    fun filterData(day: String) {
        _dashboardCategoryData.value = when (day) {
            days[0] -> dummyDataToday
            days[1] -> dummyDataYesterday
            days[2] -> dummyDataThisWeek
            days[3] -> dummyDataThisMonth
            else -> dummyDataToday
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
            DashboardBreakdownEvent.NavigateBack -> {navigator.popBackStack()}
        }
    }
}