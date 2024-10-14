package com.example.grocerychecklist.ui.screen.dashboard

import ItemCategory
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import com.example.grocerychecklist.R
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.ChecklistComponent
import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DashboardBreakdownScreen() {
    Scaffold(
        topBar = { TopBarComponent(title = "Dashboard") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
                .padding(top = 8.dp, bottom = 8.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 6.dp, top = 8.dp, end = 6.dp, bottom = 8.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFECECEC),
                        shape = RoundedCornerShape(size = 11.dp)
                    ),

                horizontalAlignment = Alignment.CenterHorizontally

                ){
                ColumnChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 8.dp),
                    data = listOf(
                        Bars(
                            label = "Apr 1",
                            values = listOf(
                                Bars.Data(
                                    value = 12.0, color = SolidColor(Color(0xFF6FA539))
                                )
                            )
                        ),
                        Bars(
                            label = "Apr 14",
                            values = listOf(
                                Bars.Data(
                                    value = 19.0, color = SolidColor(Color(0xFF6FA539))
                                )
                            )
                        ),
                        Bars(
                            label = "Apr 28",
                            values = listOf(
                                Bars.Data(
                                    value = 25.0, color = SolidColor(Color(0xFF6FA539))
                                )
                            )
                        ),
                        Bars(
                            label = "May 5",
                            values = listOf(
                                Bars.Data(
                                    value = 28.0, color = SolidColor(Color(0xFF6FA539))
                                )
                            )
                        ),
                        Bars(
                            label = "May 19",
                            values = listOf(
                                Bars.Data(
                                    value = 28.0, color = SolidColor(Color(0xFF6FA539))
                                )
                            )
                        )
                    ),

                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                        spacing = 1.dp,
                        thickness = 36.dp
                    ),

                    labelHelperProperties = LabelHelperProperties(enabled = false),

                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),

                    maxValue = 30.0
                )
            }

            Column {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    ){

                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium)

                    //TODO: Add dropdown for dates
                }

                Column (
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),

                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),

                    ) {
                    DashboardCategoryBreakDownComponent(ItemCategory.MEAT, 0.5f, 6502)
                    DashboardCategoryBreakDownComponent(ItemCategory.FRUIT, 0.3f, 5502)
                    DashboardCategoryBreakDownComponent(ItemCategory.VEGETABLE, 0.2f, 4502)
                    DashboardCategoryBreakDownComponent(ItemCategory.MEDICINE, 0.15f, 3502)
                    DashboardCategoryBreakDownComponent(ItemCategory.POULTRY, 0.1f, 2502)
                    DashboardCategoryBreakDownComponent(ItemCategory.CLEANING, 0.05f, 1502)

                }
            }
        }
    }
}

@Composable
fun DashboardCategoryBreakDownComponent(category: ItemCategory, percent: Float, value: Int){
    Row {
        Row (
            modifier = Modifier
                .weight(1.5f),

            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Box(modifier = Modifier
                .drawWithCache {
                    onDrawBehind {
                        drawCircle(
                            color = category.color,
                            radius = size.minDimension / 2,
                            center = Offset(
                                size.width / 2,
                                size.height / 2
                            )
                        )
                    }
                }
                .width(12.dp)
                .height(12.dp)
            ) {}

            Text(
                text = category.text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                )
            )
        }
        
        Row (
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 4.dp)
            .weight(2f),

            verticalAlignment = Alignment.CenterVertically,
            ){

            LinearProgressIndicator(
                progress = { percent },
                color = category.color,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Row (
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.End
        ){ Text(
            text = "₱ $value",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF565656),
                textAlign = TextAlign.End
            )
        ) }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardBreakdownPreview() {
    DashboardBreakdownScreen()
}