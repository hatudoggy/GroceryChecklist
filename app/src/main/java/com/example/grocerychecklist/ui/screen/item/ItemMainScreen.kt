package com.example.grocerychecklist.ui.screen.item

import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.FullHeightDialogComponent
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.item.ItemMainEvent
import com.example.grocerychecklist.viewmodel.item.ItemMainState

@Composable
fun ItemMainScreen(
    state: ItemMainState,
    onEvent: (ItemMainEvent) -> Unit,
) {

    if (state.isAddingItem) {
        ItemDialogComponent(
            onDismissRequest = { onEvent(ItemMainEvent.CloseDialog) },
            onSave = { name, price, category ->
                val newItem = ItemInput(
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    category = category.name,
                    measureType = "pcs",
                    measureValue = 1.0,
                    photoRef = ""
                )
                onEvent(ItemMainEvent.AddItem(newItem))
            }
        )
    }

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onEvent(ItemMainEvent.OpenDialog) },
                containerColor = PrimaryGreenSurface
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
                if (state.items.isEmpty()) {
                    item {
                        Text(
                            text = "No items available",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(state.items.size) { index ->
                        val item = state.items[index]
                        ChecklistItemComponent(
                            name = item.name,
                            variant = ChecklistItemComponentVariant.Item,
                            category = ItemCategory.valueOf(item.category),
                            price = item.price,
                            quantity = item.measureValue
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemMainScreenPreview() {
    val mockState = ItemMainState(

    )
   ItemMainScreen(
       state = mockState,
       onEvent = {}
   )
}

@Composable
fun ItemDialogComponent(
    onDismissRequest: () -> Unit,
    onSave: (String, String, ItemCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ItemCategory.OTHER) } // Default category

    FullHeightDialogComponent(
        onDismissRequest,
        scaffoldTopBar = {
            ChecklistDialogTopBarComponent(onDismissRequest, onSave = {
                onSave(name, price, selectedCategory)
            })
        },
        scaffoldContent = {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: ItemCategory,
    onCategorySelected: (ItemCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory.text,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Category") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Icon"
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ItemCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category.text, color = category.color) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ChecklistDialogTopBarComponent(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(18.dp, 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDismissRequest) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
        TextButton(onClick = onSave) {
            Text("Save")
        }
    }
}


@Composable
fun ChecklistDialogContentComponent(
    innerPadding: PaddingValues,
    //dialogInputs: ItemMainDialogInputs
) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(18.dp, 0.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Item", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Category", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Price", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}