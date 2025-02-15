package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes

class ChecklistDetailViewModel(
    private val navigator: Navigator,
    entry: NavBackStackEntry
): ViewModel() {
    val checklistId = entry.toRoute<Routes.ChecklistDetail>().checklistId

    fun onEvent(event: ChecklistDetailEvent) {
        when (event) {
            ChecklistDetailEvent.NavigateBack -> {navigator.popBackStack()}
            ChecklistDetailEvent.NavigateViewMode -> {navigator.navigate(Routes.ChecklistView)}
            ChecklistDetailEvent.NavigateEditMode -> {navigator.navigate(Routes.ChecklistEdit(checklistId))}
            ChecklistDetailEvent.NavigateStartMode -> {navigator.navigate(Routes.ChecklistStart(checklistId))}
        }
    }
}