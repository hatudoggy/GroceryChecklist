package com.example.grocerychecklist.ui.screen.dashboard

import ItemCategory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainEvent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainState
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardMainScreen(
    state: DashboardMainState,
    onEvent: (DashboardMainEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "Dashboard") },
    ) {
        innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val category = ItemCategory.entries


            Box(
                modifier = Modifier
                    .padding(vertical = 28.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    pieChartData = PieChartData(
                        state.categoryBreakdown.map {
                            PieChartData.Slice(
                                it.sumOfPrice.toFloat(),
                                ItemCategory.valueOf(it.category).color,
                            )
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    sliceDrawer = SimpleSliceDrawer(14F)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val converter = ConvertNumToCurrency()
                    Text(
                        DateUtility.formatDateMonthOnly(state.currentDate),
                        fontSize = 16.sp, color = Color.Gray
                    )
                    Text(converter(Currency.PHP, state.monthTotalPrice, false), fontSize = 34.sp)
                    Text(
                        "View More >",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = {
                            onEvent(DashboardMainEvent.ViewMoreBtn)
                        })
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.width(250.dp)
            ) {
                state.categoryBreakdown.forEach {
                    ChartLegendComponent(
                        ItemCategory.valueOf(it.category).text,
                        ItemCategory.valueOf(it.category).color
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .weight(1f, fill = true)
                    
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("History", fontSize = 18.sp)
                    Text(
                        "View All",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = {
                            onEvent(DashboardMainEvent.NavigateHistoryMain)
                        })
                    )
                }
                state.histories.forEach { item ->
                    ButtonCardComponent(
                        name = item.history.name,
                        expense = item.totalPrice,
                        date = DateUtility.formatDateWithDay(item.history.createdAt),
                        icon = item.history.icon.imageVector,
                        iconBackgroundColor = item.history.iconColor.color,
                        variant = ButtonCardComponentVariant.History,
                        onClick = {
                            onEvent(DashboardMainEvent.NavigateHistoryDetail(item.history.id))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ChartLegendComponent(
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Circle,
            modifier = Modifier
                .size(12.dp),
            tint = color,
            contentDescription = "Legend Color"
        )
        Spacer(Modifier.width(4.dp))
        Text(label)
    }
}


@Preview(showBackground = true)
@Composable
fun DashboardMainScreenPreview() {

    val mockState = DashboardMainState(
        histories = listOf()
    )
    val mockOnEvent: (DashboardMainEvent) -> Unit = {}

    DashboardMainScreen(
        state = mockState,
        onEvent = mockOnEvent
    )
}