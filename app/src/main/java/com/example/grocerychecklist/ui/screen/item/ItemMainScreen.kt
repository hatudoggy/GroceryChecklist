package com.example.grocerychecklist.ui.screen.item

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.item.ItemMainViewModel

@Composable
fun ItemMainScreen() {
    val viewModel: ItemMainViewModel = viewModel()

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    /*TODO*/
                }
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
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
                        name = "Tender Juicy Hot dog",
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