package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator

class ChecklistViewViewModel(
    private val navigator: Navigator
): ViewModel() {

    fun onEvent(event: ChecklistViewEvent) {
        when (event) {
            ChecklistViewEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}