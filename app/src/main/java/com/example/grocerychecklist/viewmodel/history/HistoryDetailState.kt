package com.example.grocerychecklist.viewmodel.history

import com.example.grocerychecklist.ui.screen.history.HistoryData

data class HistoryDetailState (
    val historyItems: List<HistoryData> = listOf(),
    val total: Double = 0.00
)