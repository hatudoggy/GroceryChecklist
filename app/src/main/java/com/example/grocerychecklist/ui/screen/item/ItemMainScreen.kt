package com.example.grocerychecklist.ui.screen.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent

@Composable
fun ItemMainScreen() {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "Items") },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
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
            LazyColumn {
                items(5) {
                    ChecklistItemComponent(
                        name = "Tender Juicy Hotdog",
                        variant = ChecklistItemComponentVariant.ChecklistItem,
                        category = "Frozen Meat",
                        price = 250.00,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemMainScreenPreview() {
   ItemMainScreen()
}