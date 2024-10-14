package com.example.grocerychecklist.ui.screen.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryMainScreen() {

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
            val months = listOf("This Month", "Sept 2024", "Aug 2024")

            months.forEach { month ->
                item {
                    Text(month, color = Color.Gray)
                }
                items(5) {
                    ChecklistComponent(
                        name = "Main Grocery",
                        expense = 400.00,
                        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                        icon = Icons.Filled.Android,
                        iconColor = MaterialTheme.colorScheme.primary,
                        variant = ChecklistComponentVariant.History,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryMainScreenPreview() {
    HistoryMainScreen()
}