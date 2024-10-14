package com.example.grocerychecklist.ui.screen.history

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.component.ChecklistComponent
import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import  androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.CollapsibleComponent
import com.example.grocerychecklist.ui.theme.Typography
import java.util.Date

data class HistoryData(
    val id: Int,
    val title: String,
    val date: String,
    val expense: Double,
    val details: List<HistoryDataDetails>
)

data class HistoryDataDetails(
    val detailsTitle: String, val detailsExpense: Double
)

val historyData = listOf(
    HistoryData(
        id = 0,
        title = "Main Grocery",
        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        expense = 400.0,
        details = listOf(
            HistoryDataDetails(
                detailsTitle = "Groceries", detailsExpense = 250.0
            ), HistoryDataDetails(
                detailsTitle = "Fruits", detailsExpense = 150.0
            )
        )
    ), HistoryData(
        id = 2,
        title = "Second Grocery",
        date = LocalDate.now().minusMonths(2).format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        expense = 450.0,
        details = listOf(
            HistoryDataDetails(
                detailsTitle = "Vegetables", detailsExpense = 200.0
            ), HistoryDataDetails(
                detailsTitle = "Dairy Products", detailsExpense = 250.0
            )
        )
    ), HistoryData(
        id = 3,
        title = "Third Grocery",
        date = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        expense = 450.0,
        details = listOf(
            HistoryDataDetails(
                detailsTitle = "Vegetables", detailsExpense = 200.0
            ), HistoryDataDetails(
                detailsTitle = "Dairy Products", detailsExpense = 250.0
            )
        )
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryMainScreen() {
    val cardStates = remember { mutableStateMapOf<Int, Boolean>() }
    val monthsList = remember { mutableListOf<String>() }
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

    val sortedHistoryData = historyData.sortedByDescending {
        LocalDate.parse(it.date, DateTimeFormatter.ofPattern("MMM dd yyyy"))
    }

    sortedHistoryData.forEach { data ->
        val month = data.date.split(" ")[0] + " " + data.date.split(" ")[2]

        if (!monthsList.contains(month)) {
            monthsList.add(month)
        }
    }

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "History") },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            monthsList.forEach { month ->
                val displayMonth = if (isCurrentMonth(month)) "This Month" else month

                item {
                    Text(
                        text = displayMonth, color = Color.Gray
                    )
                }

                items(historyData) { data ->
                    val isCardClicked = cardStates[data.id] ?: false

                    if (areDatesMatching(month, data.date)) {
                        CollapsibleComponent(
                            isCardClicked,
                            cardComponent = {
                                ChecklistComponent(
                                    name = data.title,
                                    expense = data.expense,
                                    date = data.date,
                                    icon = Icons.Filled.Android,
                                    iconColor = MaterialTheme.colorScheme.primary,
                                    variant = ChecklistComponentVariant.History,
                                    onClick = {
                                        cardStates[data.id] = !isCardClicked
                                    },
                                    isClicked = isCardClicked
                                )
                            },
                            collapsedComponent = {
                                HistoryCollapsedComponent(data, converter)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

            }
        }
    }
}

@Composable
fun HistoryCollapsedComponent(data: HistoryData, converter: ConvertNumToCurrency) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row {
            Spacer(Modifier.fillMaxWidth(0.15f))
            Column {
                data.details.forEach { details ->
                    ListItem(headlineContent = {
                        Text(details.detailsTitle, color = Color.Gray)
                    }, trailingContent = {
                        Text(
                            converter(
                                Currency.PHP,
                                details.detailsExpense
                            )
                        )
                    })

                    // Remove this divider if it is the last item
                    if (details != data.details.last())
                        Divider()
                }
            }
        }

        OutlinedButton(onClick = {}) {
            Text("See More")
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HistoryMainScreenPreview() {
    HistoryMainScreen()
}