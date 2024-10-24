package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.ui.screen.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class HistoryDetailViewModel(
    private val navigator: Navigator
) : ViewModel() {
    private val historyItems: List<HistoryItem> = listOf(
        HistoryItem(
            id = 1,
            historyId = 1, // Assuming a History record with ID 1 exists
            checklistItemId = 101, // Assuming a ChecklistItem with ID 101 exists
            name = "Milk",
            price = 50.0,
            category = "Dairy",
            measureType = "Liter",
            measureValue = 1.0,
            photoRef = "milk.jpg",
            order = 1,
            quantity = 2,
            isChecked = true,
            createdAt = LocalDateTime.now()
        ),
        HistoryItem(
            id = 2,
            historyId = 1, // Same History record
            checklistItemId = 102,
            name = "Bread",
            price = 25.0,
            category = "Bakery",
            measureType = "Piece",
            measureValue = 1.0,
            photoRef = "bread.jpg",
            order = 2,
            quantity = 1,
            isChecked = false,
            createdAt = LocalDateTime.now()
        ),
        HistoryItem(
            id = 3,
            historyId = 2, // Different History record
            checklistItemId = 103,
            name = "Eggs",
            price = 100.0,
            category = "Poultry",
            measureType = "Dozen",
            measureValue = 1.0,
            photoRef = "eggs.jpg",
            order = 1,
            quantity = 1,
            isChecked = true,
            createdAt = LocalDateTime.now()
        ),
        HistoryItem(
            id = 4,
            historyId = 2,
            checklistItemId = 104,
            name = "Chicken Breast",
            price = 150.0,
            category = "Meat",
            measureType = "Kilogram",
            measureValue = 1.5,
            photoRef = "chicken_breast.jpg",
            order = 2,
            quantity = 1,
            isChecked = false,
            createdAt = LocalDateTime.now()
        )
    )
    var items: List<HistoryItem> = historyItems
    val total = total()

    private fun total(): Double {
        var total = 0.0

        for (item in historyItems) {
            total += item.price * item.quantity
        }

        return total
    }

    fun filterItemsByCategory(selectedCategory: ItemCategory) {
        if (selectedCategory == ItemCategory.ALL) {
            items = historyItems
        }

        items =
            historyItems.filter { historyItems -> historyItems.category == selectedCategory.text }
    }

    private val _state = MutableStateFlow(HistoryDetailState())
    val state: StateFlow<HistoryDetailState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        HistoryDetailState()
    )


        fun onEvent(event: HistoryDetailEvent) {
        when (event) {
            HistoryDetailEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}