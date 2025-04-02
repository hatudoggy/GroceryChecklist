package com.example.grocerychecklist.ui.screen.history

import ItemCategory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.domain.utility.ItemCategoryUtility
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.CollapsibleComponent
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.viewmodel.history.HistoryMainEvent
import com.example.grocerychecklist.viewmodel.history.HistoryMainState
import com.example.grocerychecklist.viewmodel.history.HistoryMainViewModel

data class HistoryDataDetails(
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Double,
    val measurement: Measurement
) {
    val totalPrice: Double = price * quantity
}

@Composable
fun HistoryMainScreen(
    state: HistoryMainState,
    onEvent: (HistoryMainEvent) -> Unit,
) {

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "History") },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            when {
                state.isLoading -> {
                    // Loading indicator
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading history...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
                state.cards.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    ) {
                        state.monthsList.forEach { month ->
                            val displayMonth =
                                if (DateUtility.isCurrentMonth(month)) "This Month" else month

                            item {
                                Text(
                                    text = displayMonth, color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.5.dp))
                            }

                            items(state.cards) { data ->
                                val cardClickedState = state.cardStates[data.history.id]
                                val isCardClicked = cardClickedState == true

                                if (DateUtility.areDatesMatching(
                                        month,
                                        DateUtility.formatDate(data.history.createdAt)
                                    )
                                ) {
                                    CollapsibleComponent(
                                        isCardClicked,
                                        cardComponent = {
                                            ButtonCardComponent(
                                                name = data.history.name,
                                                expense = data.totalPrice,
                                                date = DateUtility.formatDateWithDay(data.history.createdAt),
                                                icon = data.history.icon.imageVector,
                                                iconBackgroundColor = data.history.iconColor.color,
                                                variant = ButtonCardComponentVariant.History,
                                                onClick = {
                                                    onEvent(HistoryMainEvent.ToggleCard(data.history.id))
                                                },
                                                isClicked = isCardClicked
                                            )
                                        },
                                        collapsedComponent = {
                                            HistoryCollapsedComponent(data, onEvent)
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
                else -> {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "Empty Checklist",
                                modifier = Modifier.size(32.dp),
                            )
                            Text(
                                text = "No History Items",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // Show error message if there is one
            state.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryCollapsedComponent(
    data: HistoryMapped,
    onEvent: (HistoryMainEvent) -> Unit,
    converter: ConvertNumToCurrency = ConvertNumToCurrency(),
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Spacer(Modifier.fillMaxWidth(0.15f))
            Column {
                data.aggregatedItems.forEach { details ->
                    ListItem(headlineContent = {
                        Text(
                            details.category,
                            color = Color.Black.copy(alpha = 0.5f),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    ItemCategoryUtility.getItemCategoryFromString(details.category)?.color
                                        ?: ItemCategory.OTHER.color
                                )
                                .padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }, trailingContent = {
                        Text(
                            converter(
                                Currency.PHP,
                                details.sumOfPrice
                            ),
                            fontSize = 13.sp,
                        )
                    })

                    // Remove this divider if it is the last item
                    if (details != data.aggregatedItems.last())
                        HorizontalDivider()
                }
            }
        }

        TextButton(onClick = {}) {
            Text("See More", modifier = Modifier.clickable(onClick = {
                onEvent(HistoryMainEvent.NavigateHistory(data.history.id))
            }))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryMainScreenPreview() {
    val viewModel: HistoryMainViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    HistoryMainScreen(
        state = state,
        onEvent = {}
    )
}