package com.example.grocerychecklist.ui.screen.history

import HistoryItemComponent
import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.CategorySelector
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.history.HistoryDetailEvent
import com.example.grocerychecklist.viewmodel.history.HistoryDetailState
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel

@Composable
fun HistoryDetailScreen(
    viewModel: HistoryDetailViewModel
) {
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    HistoryDetailScreen(
        state = state,
        onNavigateBackClick = { onEvent(HistoryDetailEvent.NavigateBack) },
        onSelectCategory = { onEvent(HistoryDetailEvent.SelectCategory(it)) }
    )
}

@Composable
fun HistoryDetailScreen(
    state : HistoryDetailState,
    onNavigateBackClick: () -> Unit,
    onSelectCategory: (ItemCategory) -> Unit,
) {

    Scaffold(
        topBar = {
            TopBarComponent(
                title = "History",
                onNavigateBackClick = { onNavigateBackClick }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
        ) {
            HistoryListHeader(checklist = state.checklistName, date = state.date)

            // Category selector component that allows the user to filter the list by categories.
            CategorySelector(
                selectedCategories = state.selectedCategories,
                onCategorySelected = { onSelectCategory(it)}
            )


            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.filteredItems) { historyItem ->
                    HistoryItemComponent(historyItem = historyItem)

                }
            }
            TotalSection(total = state.filteredItems.sumOf { it.price * it.quantity })
        }
    }
}


@Composable
fun HistoryListHeader(checklist: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 6.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = checklist,
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

@Preview(showBackground = true)
@Composable
fun HistoryDetailScreenPreview() {
    val navigator = Navigator()

    HistoryDetailScreen(
        state = HistoryDetailState(
            checklistName = "Grocery List",
            date = "2023-08-01",
            selectedCategories = emptySet(),
            filteredItems = emptyList()
        ),
        onNavigateBackClick = { navigator.navigate(Routes.DashboardMain) },
        onSelectCategory = {}
    )
}