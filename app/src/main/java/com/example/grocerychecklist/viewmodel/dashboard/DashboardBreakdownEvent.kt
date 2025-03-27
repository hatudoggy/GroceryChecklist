package com.example.grocerychecklist.viewmodel.dashboard

sealed interface DashboardBreakdownEvent {
    data class SelectDay(val selectedDay: String) : DashboardBreakdownEvent
    data object NavigateBack: DashboardBreakdownEvent
}