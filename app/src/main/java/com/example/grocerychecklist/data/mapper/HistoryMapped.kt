package com.example.grocerychecklist.data.mapper

import androidx.room.Embedded
import com.example.grocerychecklist.data.model.History

data class HistoryMapped(
    @Embedded val history: History,
    val totalPrice: Double = 0.0,
    val aggregatedItems: List<HistoryItemAggregated>
)

data class HistoryPriced(
    @Embedded val history: History,
    val totalPrice: Double = 0.0,
)

data class HistoryItemAggregated(
    val sumOfPrice: Double,
    val totalItems: Int,
    val category: String,
)
