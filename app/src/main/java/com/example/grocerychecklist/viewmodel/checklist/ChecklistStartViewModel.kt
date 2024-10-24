package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator

class ChecklistStartViewModel(
    private val navigator: Navigator
): ViewModel() {

    fun onEvent(event: ChecklistStartEvent) {
        when (event) {
            ChecklistStartEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}