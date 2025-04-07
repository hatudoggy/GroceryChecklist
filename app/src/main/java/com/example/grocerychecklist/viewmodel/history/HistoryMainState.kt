package com.example.grocerychecklist.viewmodel.history

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.grocerychecklist.data.mapper.HistoryMapped

data class HistoryMainState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cards: List<HistoryMapped> = emptyList(),
    val monthsList: List<String> = emptyList(),
    val cardStates: Map<Long, Boolean> = emptyMap()
)
