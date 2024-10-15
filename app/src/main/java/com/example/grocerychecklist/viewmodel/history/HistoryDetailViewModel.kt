package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.data.model.HistoryItem

class HistoryDetailViewModel: ViewModel() {
    private val historyItems: List<HistoryItem> = TODO()
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

        items = historyItems.filter { historyItems -> historyItems.category == selectedCategory.text }
    }
}