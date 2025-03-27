package com.example.grocerychecklist.viewmodel.checklist

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.util.SearchableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.contains

class ChecklistViewViewModel(
    private val navigator: Navigator,
    private val repository: ChecklistItemRepository,
    entry: NavBackStackEntry,
): SearchableViewModel<ChecklistData>(matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}) {

    val checklistId = entry.toRoute<Routes.ChecklistView>().checklistId

    private val _state = MutableStateFlow(ChecklistViewState())
    val state: StateFlow<ChecklistViewState> = combine(
        _state, filteredItems, searchQuery
    ) { currentState, items, query ->
        currentState.copy(checklistItems = items, searchQuery = query)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ChecklistViewState()
    )

    init {
        loadChecklistItems()
    }

    private fun loadChecklistItems() {
        viewModelScope.launch {
            repository.getChecklistItems(checklistId, ChecklistItemOrder.Name)
                .collect {setItems(checklistDataMapper(it))}
        }
    }

    fun onEvent(event: ChecklistViewEvent) {
        when (event) {
            ChecklistViewEvent.NavigateBack -> { navigator.popBackStack() }
            is ChecklistViewEvent.UpdateSearchQuery -> onSearchQueryChanged(event.query)
        }
    }
}