package com.example.grocerychecklist.viewmodel.dashboard

sealed interface DashboardBreakdownEvent {
    data object NavigateBack: DashboardBreakdownEvent
}