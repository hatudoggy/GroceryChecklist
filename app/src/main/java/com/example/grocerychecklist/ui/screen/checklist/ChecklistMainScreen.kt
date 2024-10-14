package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.FloatingActionButton
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
import androidx.navigation.compose.NavHost
import com.example.grocerychecklist.ui.component.BottomBarComponent
import com.example.grocerychecklist.ui.component.ChecklistComponent
import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import com.example.grocerychecklist.ui.component.ChecklistIconComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.dashboard.dashboardDestination
import com.example.grocerychecklist.ui.screen.history.historyDestination
import com.example.grocerychecklist.ui.screen.item.itemDestination
import com.example.grocerychecklist.ui.screen.settings.settingsDestination
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ChecklistMainScreen() {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {}
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = { TopBarComponent(title = "Checklist") },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(5) {
                ChecklistComponent(
                    name = "Main Grocery",
                    description = "Description",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Filled.Android,
                    iconColor = MaterialTheme.colorScheme.primary,
                    variant = ChecklistComponentVariant.Checklist,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreview() {
    ChecklistMainScreen()
}
