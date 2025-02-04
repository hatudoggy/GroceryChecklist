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
import com.example.grocerychecklist.ui.component.Measurement
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
    val currentState: HistoryDetailState = demoState
    
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
            HistoryListHeader(title = "Main Grocery", date = "August 25, 2024")
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                filteredItems.forEach { historyItem ->
                    item {
                        HistoryItemComponent(historyItem = historyItem)
                    }
                }

            }
            TotalSection(total = currentState.total)
        }
    }
}

@Composable
fun HistoryListHeader(title: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 6.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF000000)
            )
        )

        Text(
            text = date,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFA5A5A5)
            )
        )
    }
}

@Composable
fun TotalSection(total: Double) {
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
                color = Color(0xFFFFFFFF)
            ),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "₱ $total", // Use string template for better readability
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF)
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}


private fun updateSelectedCategories(
    category: ItemCategory,
    currentCategories: Set<ItemCategory>
): Set<ItemCategory> {
    return when {
        // If ALL is selected, deselect all other categories
        category == ItemCategory.ALL -> {
            if (currentCategories.contains(ItemCategory.ALL)) {
                emptySet() // Deselect ALL if clicked again
            } else {
                setOf(ItemCategory.ALL) // Select ALL
            }
        }

        // If ALL was previously selected, replace it with the new category
        currentCategories.contains(ItemCategory.ALL) -> setOf(category)

        // Toggle category selection for other categories
        currentCategories.contains(category) -> currentCategories - category
        else -> currentCategories + category
    }
}

private fun filterItemsByCategory(
    historyItems: List<HistoryDataDetails>,
    selectedCategories: Set<ItemCategory>
): List<HistoryDataDetails> {
    return if (selectedCategories.contains(ItemCategory.ALL)) {
        historyItems
    } else {
        historyItems.filter { it.category in selectedCategories }
    }
}

val demoState = HistoryDetailState(
    historyItems = listOf(
        HistoryDataDetails(
            name = "Eggs",
            category = ItemCategory.POULTRY,
            price = 120.0,
            quantity = 1.00,
            measurement = Measurement.DOZEN
        ),
        HistoryDataDetails(
            name = "Tomatoes",
            category = ItemCategory.VEGETABLE,
            price = 20.0,
            quantity = 1.00,
            measurement = Measurement.KILOGRAM
        ),
        HistoryDataDetails(
            name = "Pork",
            category = ItemCategory.MEAT,
            price = 150.0,
            quantity = 1.00,
            measurement = Measurement.KILOGRAM
        ),
        HistoryDataDetails(
            name = "Rice",
            category = ItemCategory.GRAIN,
            price = 30.0,
            quantity = 1.00,
            measurement = Measurement.KILOGRAM
        ),
        HistoryDataDetails(
            name = "Ice Cream",
            category = ItemCategory.DAIRY,
            price = 100.0,
            quantity = 1.00,
            measurement = Measurement.TUB
        )
    ))


@Preview(showBackground = true)
@Composable
fun HistoryDetailScreenPreview() {
    val mockState = demoState

    HistoryDetailScreen(
        state = mockState,
        onEvent = {}
    )
}