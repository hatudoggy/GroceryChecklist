package com.example.grocerychecklist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

abstract class SearchableViewModel<T>(
    private val matchesSearch: (T, String) -> Boolean
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allItems = MutableStateFlow<List<T>>(emptyList())
    val allItems: StateFlow<List<T>> = _allItems.asStateFlow()

    val filteredItems: StateFlow<List<T>> = combine(
        allItems, searchQuery
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter { matchesSearch(it, query) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setItems(items: List<T>) {
        _allItems.value = items
    }
}
