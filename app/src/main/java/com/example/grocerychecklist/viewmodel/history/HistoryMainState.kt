package com.example.grocerychecklist.viewmodel.history

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.grocerychecklist.data.mapper.HistoryMapped

data class HistoryMainState (
    // Main Data
    val cards: List<HistoryMapped> = emptyList(),
    val cardStates: MutableMap<Long, Boolean> = mutableStateMapOf(),
    val monthsList: MutableList<String> = mutableStateListOf(),
)
