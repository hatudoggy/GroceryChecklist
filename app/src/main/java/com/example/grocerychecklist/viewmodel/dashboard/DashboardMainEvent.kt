package com.example.grocerychecklist.viewmodel.dashboard

sealed interface DashboardMainEvent {
    data object AddChecklist: DashboardMainEvent
}