package com.example.grocerychecklist.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator

class DashboardBreakdownViewModel(
    private val navigator: Navigator
): ViewModel() {
    fun onEvent(event: DashboardBreakdownEvent) {
        when (event) {
            DashboardBreakdownEvent.NavigateBack -> {navigator.popBackStack()}
        }
    }
}