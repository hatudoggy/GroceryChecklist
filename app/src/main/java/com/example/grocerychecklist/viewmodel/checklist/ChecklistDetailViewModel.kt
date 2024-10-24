package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes

class ChecklistDetailViewModel(
    private val navigator: Navigator
): ViewModel() {

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            ChecklistDetailEvent.NavigateViewMode -> {navigator.navigate(Routes.ChecklistView)}
            ChecklistDetailEvent.NavigateEditMode -> {navigator.navigate(Routes.ChecklistEdit)}
            ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(Routes.ChecklistStart)}
        }
    }
}