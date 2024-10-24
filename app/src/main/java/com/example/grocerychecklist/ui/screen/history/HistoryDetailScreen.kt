package com.example.grocerychecklist.ui.screen.history

import HistoryItemComponent
import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.HistoryChipComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.history.HistoryDetailEvent
import com.example.grocerychecklist.viewmodel.history.HistoryDetailState
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HistoryDetailScreen(
    state: HistoryDetailState,
    onEvent: (HistoryDetailEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopBarComponent(
                title = "History",
                onNavigateBackClick = { onEvent(HistoryDetailEvent.NavigateBack) }
            )
        },
    ) {
        innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 6.dp, end = 16.dp),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Main Grocery",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                    )
                )

                Text(
                    text = "August 25, 2024",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA5A5A5),
                    )
                )
            }

            var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

            LazyRow(
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(ItemCategory.entries) { category ->
                    HistoryChipComponent(
                        category = category,
                        isSelected = category == selectedCategory,
                        onSelected = {
                            selectedCategory = category
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item { HistoryItemComponent()}
                item { HistoryItemComponent()}
                item { HistoryItemComponent()}
                item { HistoryItemComponent()}
                item { HistoryItemComponent()}
                item { HistoryItemComponent()}

            }
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .height(48.dp)
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier.weight(1f) // This pushes the text to the left
                )

                Text(
                    text = "₱ " + state.total,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier.weight(1f), // This pushes the text to the right
                    textAlign = TextAlign.End // Align the text to the right
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryDetailScreenPreview() {
    val mockState = HistoryDetailState(
        historyItems = listOf(
            HistoryData(
                id = 0,
                title = "Main Grocery",
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                expense = 400.0,
                details = listOf(
                    HistoryDataDetails(
                        detailsTitle = "Poultry", detailsExpense = 250.0
                    ), HistoryDataDetails(
                        detailsTitle = "Medicine", detailsExpense = 150.0
                    )
                )
            ), HistoryData(
                id = 1,
                title = "Second Grocery",
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                expense = 400.0,
                details = listOf(
                    HistoryDataDetails(
                        detailsTitle = "Poultry", detailsExpense = 250.0
                    ), HistoryDataDetails(
                        detailsTitle = "Medicine", detailsExpense = 150.0
                    )
                )
            ),
        ),
        total = 200.0,
    )
    HistoryDetailScreen(
        state = mockState,
        onEvent = {}
    )
}