package com.example.grocerychecklist.viewmodel.dashboard

data class DashboardBreakdownState(
    val days: List<String> = listOf("Today", "Yesterday", "This Week", "This Month"),
    val selectedDay: String = "Today",
    val dashboardCategoryData: List<DashboardCategoryData> = emptyList(),
    val dashboardGraphData: List<DashboardGraphData> = emptyList(),
    val dashboardGraphMaxValue: Double = 1000.0,
    val errorMessage: String? = null,
)