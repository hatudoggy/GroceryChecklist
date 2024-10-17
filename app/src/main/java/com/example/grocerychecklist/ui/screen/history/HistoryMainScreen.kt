package com.example.grocerychecklist.ui.screen.history

import ItemCategory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.CollapsibleComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.theme.SkyGreen
import com.example.grocerychecklist.viewmodel.history.HistoryMainState
import com.example.grocerychecklist.viewmodel.history.HistoryMainViewModel

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
    ), HistoryData(
        id = 2,
        title = "Third Grocery",
        date = LocalDate.now().minusMonths(2).format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        expense = 450.0,
        details = listOf(
            HistoryDataDetails(
                detailsTitle = "Vegetable", detailsExpense = 200.0
            ), HistoryDataDetails(
                detailsTitle = "Meat", detailsExpense = 250.0
            )
        )
    ), HistoryData(
        id = 3,
        title = "Fourth Grocery",
        date = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        expense = 450.0,
        details = listOf(
            HistoryDataDetails(
                detailsTitle = "Sanitary", detailsExpense = 200.0
            ), HistoryDataDetails(
                detailsTitle = "Cleaning", detailsExpense = 250.0
            )
        )
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryMainScreen(
    navController: NavController,
    viewModel: HistoryMainViewModel,
    historyMainState: HistoryMainState,
) {
    val (cardStates, monthsList) = historyMainState
    val converter = viewModel.converter

    viewModel.sortHistoryData(historyData)

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "History") },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {

            monthsList.forEach { month ->
                val displayMonth = if (viewModel.isCurrentMonth(month)) "This Month" else month

                item {
                    Text(
                        text = displayMonth, color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.5.dp))
                }

                items(historyData) { data ->
                    val isCardClicked = cardStates[data.id] ?: false

                    if (viewModel.areDatesMatching(month, data.date)) {
                        CollapsibleComponent(
                            isCardClicked,
                            cardComponent = {
                                ButtonCardComponent(
                                    name = data.title,
                                    expense = data.expense,
                                    date = data.date,
                                    icon = Icons.Filled.Fastfood,
                                    iconBackgroundColor = SkyGreen,
                                    variant = ButtonCardComponentVariant.History,
                                    onClick = {
                                        cardStates[data.id] = !isCardClicked
                                    },
                                    isClicked = isCardClicked
                                )
                            },
                            collapsedComponent = {
                                HistoryCollapsedComponent(data, converter, navController)
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }

            }
        }
    }
}

@Composable
fun HistoryCollapsedComponent(
    data: HistoryData,
    converter: ConvertNumToCurrency,
    navController: NavController
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row {
            Spacer(Modifier.fillMaxWidth(0.15f))
            Column {
                data.details.forEach { details ->
                    ListItem(headlineContent = {
                        Text(
                            details.detailsTitle,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ItemCategory.valueOf(details.detailsTitle.uppercase()).color)
                                .padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }, trailingContent = {
                        Text(
                            converter(
                                Currency.PHP,
                                details.detailsExpense
                            ),
                            fontSize = 13.sp,
                        )
                    })

                    // Remove this divider if it is the last item
                    if (details != data.details.last())
                        HorizontalDivider()
                }
            }
        }

        TextButton(onClick = {}) {
            Text("See More", modifier = Modifier.clickable(onClick = {
                navController.navigate(Routes.HistoryDetail)
            }))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryMainScreenPreview() {
    val viewModel: HistoryMainViewModel = viewModel()
    val historyMainState by viewModel.historyMainState.collectAsState()

    HistoryMainScreen(
        navController = rememberNavController(),
        viewModel,
        historyMainState
    )
}