package com.example.grocerychecklist.viewmodel.dashboard

data class DashboardBreakdownState(
    val days: List<String> = listOf("Today", "Yesterday", "This Week", "This Month"),
    val selectedDay: String = "Today",
    val dashboardCategoryData: List<DashboardCategoryData> = emptyList(),
    val errorMessage: String? = null,
)

data class DashboardGraphState(
    val data: List<DashboardGraphData> = emptyList(),
    val maxValue: Double = 0.0
)