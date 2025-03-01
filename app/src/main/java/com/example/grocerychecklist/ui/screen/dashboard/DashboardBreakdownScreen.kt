package com.example.grocerychecklist.ui.screen.dashboard

import ItemCategory
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.viewmodel.dashboard.DashboardBreakdownEvent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardBreakdownViewModel
import com.example.grocerychecklist.viewmodel.dashboard.DashboardCategoryData
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBreakdownScreen(
    //state: DashboardBreakdownState,
    viewModel: DashboardBreakdownViewModel,
    onEvent: (DashboardBreakdownEvent) -> Unit,
) {
    // Accessing days from the ViewModel.
    val days = viewModel.days
    // Collecting dashboard category data from the ViewModel as a state.
    val state by viewModel.dashboardCategoryData.collectAsState()
    // Collecting the selected day from the ViewModel as a state.
    val selectedDay by viewModel.selectedDay.collectAsState()

    Scaffold(
        topBar = { TopBarComponent(
            title = "Dashboard",
            onNavigateBackClick = {onEvent(DashboardBreakdownEvent.NavigateBack)})
         },
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
                    .padding(start = 6.dp, top = 8.dp, end = 6.dp, bottom = 4.dp)
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
                // State variables to manage the dropdown's expanded state and the selected option text.
                // `expanded` controls the visibility of the dropdown menu.
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf(selectedDay.first()) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = { },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = if (!expanded) Color.Transparent else Color.LightGray,
                                focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                                unfocusedIndicatorColor = Color.Transparent // Remove underline when not focused
                            ),
                            textStyle =  MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.End
                            ),

                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            days.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                        viewModel.filterData(selectionOption)
                                    },
                                    text = { Text(selectionOption) }
                                )
                            }
                        }
                    }

                }

                LazyColumn (
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),

                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),

                    ) {

                    // Iterating through each category data to display breakdown.
                    items(state) { dashboardCategoryData ->
                        DashboardCategoryBreakDownComponent(dashboardCategoryData)
                    }
                }
            }
        }
    }
}


/**
 * Displays a breakdown of a single category on the dashboard.
 *
 * This component visualizes the category's name, a colored indicator, a progress bar representing
 * its percentage, and the associated expense value.
 *
 * @param data The [DashboardCategoryData] object containing the category's details, percentage, and expense value.
 *
 * @see DashboardCategoryData
 *
 * Example Usage:
 * ```
 * DashboardCategoryBreakDownComponent(
 *     data = DashboardCategoryData(
 *         category = DashboardCategory(
 *             text = "Food",
 *             color = Color.Red
 *         ),
 *         percent = 0.4f,
 *         expenses = 1200.0
 *     )
 * )
 * ```
 */
@Composable
fun DashboardCategoryBreakDownComponent(data: DashboardCategoryData){
    val category = data.category
    val percent = data.percent
    val value = data.expenses

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
    val navigator = Navigator()
    val viewModel = DashboardBreakdownViewModel(navigator);

    DashboardBreakdownScreen(
        viewModel = viewModel,
        onEvent = {}
    )
}