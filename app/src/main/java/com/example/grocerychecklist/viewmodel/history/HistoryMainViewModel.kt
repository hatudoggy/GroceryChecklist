package com.example.grocerychecklist.viewmodel.history

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.ui.screen.history.HistoryData
import com.example.grocerychecklist.ui.screen.history.historyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryMainViewModel : ViewModel() {

    private val _historyMainState = MutableStateFlow(HistoryMainState())
    val historyMainState = _historyMainState.asStateFlow()

    val converter = ConvertNumToCurrency()

    fun isCurrentMonth(date: String): Boolean {
        val formattedDate = "$date 01"
        val inputDate = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("MMM yyyy dd"))
        val currentDate = LocalDate.now()

        return inputDate.month == currentDate.month && inputDate.year == currentDate.year
    }

    fun areDatesMatching(dateFromList: String, dateFromBackend: String): Boolean {
        return dateFromList == dateFromBackend.split(" ")[0] + " " + dateFromBackend.split(" ")[2]
    }

    fun sortHistoryData(historyData: List<HistoryData>) {
        val sortedHistoryData = historyData.sortedByDescending {
            LocalDate.parse(it.date, DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }

        sortedHistoryData.forEach { data ->
            val month = data.date.split(" ")[0] + " " + data.date.split(" ")[2]

            if (!_historyMainState.value.monthsList.contains(month)) {
                _historyMainState.value.monthsList.add(month)
            }
        }
    }
}