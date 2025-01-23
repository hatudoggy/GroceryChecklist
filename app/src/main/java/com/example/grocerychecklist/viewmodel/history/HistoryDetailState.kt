package com.example.grocerychecklist.viewmodel.history

import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails

data class HistoryDetailState(val historyItems: List<HistoryDataDetails> = listOf()) {
    val total: Double = calculateTotal(historyItems)
}

private fun calculateTotal(historyItems: List<HistoryDataDetails>): Double {
    var total = 0.0

    for (item in historyItems) {
        total += item.price * item.quantity
    }

    return total
}