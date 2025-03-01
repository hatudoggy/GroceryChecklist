package com.example.grocerychecklist.ui.screen.checklist

//import com.example.grocerychecklist.ui.component.ChecklistComponent
//import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewState

@Composable
fun ChecklistViewScreen(
    state: ChecklistViewState,
    onEvent: (ChecklistViewEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),

        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = { onEvent(ChecklistViewEvent.NavigateBack) }
            )
        },

    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Main Grocery",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Visibility,
                        modifier = Modifier
                            .size(18.dp),
                        tint = Color.LightGray,
                        contentDescription = "eye"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "View Mode",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

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
                placeholderText = "Search",
                value = state.searchQuery,
                onValueChange = { onEvent(ChecklistViewEvent.UpdateSearchQuery(it)) }
            )

            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(state.checklistItems) { item ->
                    ChecklistItemComponent(
                        name = item.name,
                        variant = ChecklistItemComponentVariant.ChecklistItem,
                        category = item.category,
                        price = item.price,
                        quantity = item.quantity.toDouble(),
                        measurement = item.measurement,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistViewScreenPreview() {
    ChecklistViewScreen(
        state = ChecklistViewState(),
        onEvent = {}
    )
}
