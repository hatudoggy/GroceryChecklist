package com.example.grocerychecklist.ui.screen.history

import HistoryItemComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.HistoryChipComponent
import com.example.grocerychecklist.ui.component.HistoryChipGroup
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.history.HistoryDetailViewModel



@Composable
fun HistoryDetailScreen(viewModel: HistoryDetailViewModel) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "History")},
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(48.dp),
                containerColor = Color(0xFF81C511),
                contentPadding = PaddingValues(start = 18.dp, top = 8.dp, end = 18.dp, bottom = 8.dp),
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
                    text = "₱ " + viewModel.total,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier.weight(1f), // This pushes the text to the right
                    textAlign = androidx.compose.ui.text.style.TextAlign.End // Align the text to the right
                )
            }
        }

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

            LazyRow(
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item {
                    HistoryChipGroup(
                        categories = ItemCategory.entries,
                        onCategorySelected = { selectedCategory ->
                            viewModel.filterItemsByCategory(selectedCategory)
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryDetailScreenPreview() {
    HistoryDetailScreen(HistoryDetailViewModel())
}