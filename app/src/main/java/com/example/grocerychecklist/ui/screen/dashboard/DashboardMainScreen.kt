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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.theme.SkyGreen
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainEvent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainState
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    pieChartData = PieChartData(listOf(
                        PieChartData.Slice(5F, category[1].color),
                        PieChartData.Slice(2F, category[2].color),
                        PieChartData.Slice(3F, category[3].color),
                    )),
                    modifier = Modifier.fillMaxWidth(),
                    sliceDrawer = SimpleSliceDrawer(18F)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val converter = ConvertNumToCurrency()
                    Text("August", fontSize = 16.sp, color = Color.Gray)
                    Text(converter(Currency.PHP, 12592.00, false), fontSize = 34.sp)
                    Text(
                        "View More >",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = {
                            onEvent(DashboardMainEvent.ViewMoreBtn)
                        })
                    )
                }
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ChartLegendComponent(
                    category[1].text,
                    category[1].color
                )
                ChartLegendComponent(
                    category[2].text,
                    category[2].color
                )
                ChartLegendComponent(
                    category[3].text,
                    category[3].color
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Text("History", fontSize = 18.sp)
                repeat(4) {
                    ButtonCardComponent(
                        name = "Main Grocery",
                        expense = 400.00,
                        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                        icon = Icons.Filled.Fastfood,
                        iconBackgroundColor = SkyGreen,
                        variant = ButtonCardComponentVariant.History,
                        onClick = {

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
    val currentDateTime = DateUtility.getCurrentDateTime()
    val mockState = DashboardMainState(
        checklists = listOf(
            Checklist(
                name = "Dog",
                description = "",
                icon = IconOption.Home,
                iconColor = ColorOption.White,
                createdAt = currentDateTime,
                updatedAt = currentDateTime,
                lastOpenedAt = currentDateTime,
                lastShopAt = currentDateTime
            )
        )
    )
    val mockOnEvent: (DashboardMainEvent) -> Unit = {}

    DashboardMainScreen(
        state = mockState,
        onEvent = mockOnEvent
    )
}