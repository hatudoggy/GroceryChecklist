package com.example.grocerychecklist.ui.screen.item

import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.ui.component.CategoryDropdown
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.FullHeightDialogComponent
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.item.ItemMainEvent
import com.example.grocerychecklist.viewmodel.item.ItemMainState
import com.example.grocerychecklist.viewmodel.item.ItemMainViewModel

@Composable
fun ItemMainScreen(
    state: ItemMainState,
    onEvent: (ItemMainEvent) -> Unit,
    viewModel: ItemMainViewModel
) {
    if (state.isAddingItem) {
        ItemDialogComponent(
            selectedItem = state.selectedItem,
            onDismissRequest = { onEvent(ItemMainEvent.CloseDialog) },
            onSave = { name, price, category ->
                val newItemInput = ItemInput(
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    category = category.name,
                    measureType = "pcs",
                    measureValue = 1.0,
                    photoRef = ""
                )
                if (state.selectedItem == null) {
                    onEvent(ItemMainEvent.AddItem(newItemInput)) // Add new item
                } else {
                    onEvent(
                        ItemMainEvent.EditItem(
                            state.selectedItem.id,
                            newItemInput
                        )
                    ) // Edit existing item
                }
            },
            onDelete = state.selectedItem?.let { { onEvent(ItemMainEvent.DeleteItem(it)) } }, // Delete if editing
            viewModel = viewModel
        )
    }

    Scaffold(
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            RoundedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.background(
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
                            category = viewModel.getItemCategoryFromString(item.category)
                                ?: ItemCategory.OTHER,
                            price = item.price,
                            quantity = item.measureValue,
                            onClick = { onEvent(ItemMainEvent.SelectItem(item)) } // Click to edit
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
        onEvent = {},
        viewModel = ItemMainViewModel(
            itemRepository = TODO()
        )
    )
}

@Composable
fun ItemDialogComponent(
    selectedItem: Item?, // Pass selected item if editing
    onDismissRequest: () -> Unit,
    onSave: (String, String, ItemCategory) -> Unit,
    onDelete: (() -> Unit)? = null,
    viewModel: ItemMainViewModel
) {
    var name by remember { mutableStateOf(selectedItem?.name ?: "") }
    var price by remember { mutableStateOf(selectedItem?.price?.toString() ?: "") }
    var selectedCategory by remember {
        mutableStateOf(selectedItem?.category?.let {
            viewModel.getItemCategoryFromString(it)
        } ?: ItemCategory.OTHER)
    }

    FullHeightDialogComponent(
        onDismissRequest,
        scaffoldTopBar = {
            ChecklistDialogTopBarComponent(
                name = name,
                onDismissRequest,
                onSave = { onSave(name, price, selectedCategory) }
            )
        },
        scaffoldContent = {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(50.dp))
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

                // Show Delete button only when editing
                selectedItem?.let {
                    TextButton(
                        onClick = { onDelete?.invoke() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                }
            }
        }
    )
}


@Composable
fun ChecklistDialogTopBarComponent(
    name: String,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDismissRequest) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
        TextButton(
            onClick = onSave,
            enabled = name.isNotBlank()
        ) {
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
            onValueChange = { },
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
            onValueChange = { },
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
            onValueChange = { },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}