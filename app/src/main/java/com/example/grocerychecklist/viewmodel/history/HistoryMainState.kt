package com.example.grocerychecklist.viewmodel.history

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

data class HistoryMainState (
    val cardStates: MutableMap<Int, Boolean> = mutableStateMapOf(),
    val monthsList: MutableList<String> = mutableStateListOf(),
)
