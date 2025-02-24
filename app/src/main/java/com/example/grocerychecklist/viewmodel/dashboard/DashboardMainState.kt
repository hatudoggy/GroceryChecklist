package com.example.grocerychecklist.viewmodel.dashboard

import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.mapper.HistoryPriced
import java.time.LocalDate

data class DashboardMainState (
    val histories: List<HistoryPriced> = emptyList(),
    val currentDate: LocalDate = LocalDate.now(),
    val monthTotalPrice: Double = 0.00,
    val categoryBreakdown: List<HistoryItemAggregated> = emptyList()
)