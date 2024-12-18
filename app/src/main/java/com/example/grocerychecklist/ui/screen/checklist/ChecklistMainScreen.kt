package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.component.ChecklistDialogComponent
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ChecklistMainScreen(
    //state: DashboardBreakdownState,
    onEvent: (ChecklistMainEvent) -> Unit,
) {
    val viewModel: ChecklistMainViewModel = viewModel()
    val dialogState by viewModel.dialogState.collectAsState()

    if (dialogState.isAddingChecklist) {
        ChecklistDialogComponent()
    }

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    viewModel.openDialog()
                },
                containerColor = PrimaryGreenSurface
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = { TopBarComponent(title = "Checklist") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            RoundedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(percent = 50)
                    ),
                fontSize = 16.sp,
                placeholderText = "Search"
            )
            Spacer(Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ButtonCardComponent(
                    name = "Main Grocery",
                    description = "A checklist of the main groceries for the month. All the essentials...",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Default.Fastfood,
                    iconBackgroundColor = ChecklistCategory.MAIN_GROCERY.color,
                    variant = ButtonCardComponentVariant.Checklist,
                    onClick = {viewModel.onEvent(ChecklistMainEvent.NavigateChecklist)}
                )
                ButtonCardComponent(
                    name = "Grandpa's Meds",
                    description = "Important to buy it weekly",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Default.Medication,
                    iconBackgroundColor = ChecklistCategory.MEDICINE.color,
                    variant = ButtonCardComponentVariant.Checklist,
                    onClick = {onEvent(ChecklistMainEvent.NavigateChecklist)}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreview() {
    ChecklistMainScreen(
        onEvent = {}
    )
}

@Composable
fun ChecklistCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    date: String,
    description: String
) {
    Row(
        modifier = Modifier
            .heightIn(min = 60.dp, max = 60.dp)
            .clickable(onClick = {
                println("Hello")
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp, 0.dp)
        ) {
            Icon(
                icon, "menu", modifier = Modifier
                    .size(42.5.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(color = iconBackgroundColor)
                    .padding(7.dp)
                    .align(Alignment.CenterVertically), tint = Color.White
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(title, fontWeight = FontWeight(600))
                    Text(
                        date,
                        fontSize = 12.sp,
                        color = Color.Gray.copy(0.8f)
                    )
                }
                Text(
                    description,
                    fontSize = 12.sp,
                    color = Color.Gray.copy(0.8f),
                    // This fixes the additional line padding when text is wrapped
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth(0.78f)
                )
            }
        }
    }
}