package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails

data class HistoryDetailState(
    val checklistName: String = "",
    val date: String = "",
    val historyItems: List<HistoryDataDetails> = listOf(),
    val selectedCategories: Set<ItemCategory> = setOf(ItemCategory.ALL),
    val filteredItems: List<HistoryDataDetails> = listOf()
)
