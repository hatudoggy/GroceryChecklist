package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.ChecklistComponent
import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardMainScreen(navController: NavController) {
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
            Box(
                modifier = Modifier
                    .padding(vertical = 28.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    pieChartData = PieChartData(listOf(
                        PieChartData.Slice(5F, Color(0xff90e0ef)),
                        PieChartData.Slice(2F, Color(0xff00b4d8)),
                        PieChartData.Slice(3F, Color(0xff0077b6)),
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
                            navController.navigate(Routes.DashboardBreakdown)
                        })
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Text("History", fontSize = 18.sp)
                repeat(5) {
                    ChecklistComponent(
                        name = "Main Grocery",
                        expense = 400.00,
                        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                        icon = Icons.Filled.Android,
                        iconColor = MaterialTheme.colorScheme.primary,
                        variant = ChecklistComponentVariant.History,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardMainScreenPreview() {
    DashboardMainScreen(navController = rememberNavController())
}