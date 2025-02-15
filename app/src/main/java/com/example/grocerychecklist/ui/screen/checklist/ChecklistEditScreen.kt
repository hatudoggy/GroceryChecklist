package com.example.grocerychecklist.ui.screen.checklist

import ItemCategory
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.ui.component.ActionMenu
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.CategoryDropdown
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent


data class ChecklistEditFormInputs (
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Int,
)

@Composable
fun ChecklistEditScreen(
    state: ChecklistEditState,
    onEvent: (ChecklistEditEvent) -> Unit,
)  {

    BottomSheetChecklistEdit(
        selectedItem = state.selectedItem,
        isOpen = state.isDrawerOpen,
        onClose = { onEvent(ChecklistEditEvent.CloseDrawer) },
        onAdd = { name, category, price, quantity ->
            if (state.selectedItem == null)
                onEvent(ChecklistEditEvent.AddChecklistItem(
                    ChecklistEditFormInputs(name, category, price, quantity)
                ))
            else
                onEvent(ChecklistEditEvent.EditChecklistItem(
                    ChecklistEditFormInputs(name, category, price, quantity)
                ))
        },
        onVisible = { visible -> if(!visible) onEvent(ChecklistEditEvent.ClearSelectedItem)}
    )

    // Edit, Delete Action Menu
    ActionMenu(
        isOpen = state.isActionMenuOpen,
        onClose = { onEvent(ChecklistEditEvent.CloseActionMenu) },
        onEditMenu = {
            onEvent(ChecklistEditEvent.OpenDrawer)
        },
        onDeleteDialog = {
            onEvent(ChecklistEditEvent.OpenDeleteDialog)
        }
    )

    // Delete Dialog
    if (state.isDeleteDialogOpen) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { onEvent(ChecklistEditEvent.CloseDeleteDialog) },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedItem.let {
                            if (it != null) {
                                onEvent(ChecklistEditEvent.DeleteChecklistItem(it.checklistItem))
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ChecklistEditEvent.CloseDeleteDialog) }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onEvent(ChecklistEditEvent.OpenDrawer) },
                containerColor = PrimaryGreenSurface
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = { onEvent(ChecklistEditEvent.NavigateBack) }
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
                        Icons.Filled.Edit,
                        modifier = Modifier
                            .size(18.dp),
                        tint = Color.LightGray,
                        contentDescription = "eye"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Edit Mode",
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
                onValueChange = { onEvent(ChecklistEditEvent.SetSearchQuery(it)) }
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(state.items) { item ->
                    ChecklistItemComponent(
                        name = item.item.name,
                        variant = ChecklistItemComponentVariant.ChecklistItem,
                        category = ItemCategory.entries.find { it.name == item.item.category } ?: ItemCategory.OTHER,
                        price = item.item.price,
                        quantity = item.checklistItem.quantity.toDouble(),
                        measurement = Measurement.entries.find { it.name == item.item.measureType } ?: Measurement.PIECE,
                        onLongPress = { onEvent(ChecklistEditEvent.OpenActionMenu(item)) }
                    )
                }
            }
        }
    }
}

// Bottom sheet modal drawer for editing Checklist
@Composable
fun BottomSheetChecklistEdit(
    selectedItem: ChecklistItemFull? = null,
    isOpen: Boolean,
    onClose: () -> Unit,
    onAdd: (String, ItemCategory, Double, Int) -> Unit,
    onVisible: (Boolean) -> Unit = {}
) {

    var name by remember { mutableStateOf(selectedItem?.item?.name ?: "") }
    var category by remember { mutableStateOf(selectedItem?.item?.category?.let { ItemCategory.valueOf(it) } ?: ItemCategory.OTHER) }
    var price by remember { mutableStateOf(selectedItem?.item?.price?.toString() ?: "") }
    var quantity by remember { mutableStateOf(selectedItem?.checklistItem?.quantity?.toString() ?: "")  }

    LaunchedEffect(isOpen) {
        if (!isOpen) {
            name = ""
            category = ItemCategory.OTHER
            price = ""
            quantity = ""
        } else {
            name = selectedItem?.item?.name ?: ""
            category = selectedItem?.item?.category?.let { ItemCategory.valueOf(it) } ?: ItemCategory.OTHER
            price = selectedItem?.item?.price?.toString() ?: ""
            quantity = selectedItem?.checklistItem?.quantity?.toString() ?: ""
        }
    }

    BottomSheet(
        isOpen = isOpen,
        onClose = onClose,
        skipExpand = true,
        onVisibilityChanged = { visible -> onVisible(visible) }
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
                )
        ) {
            Text(
                "${if(selectedItem == null) "Add" else "Edit"} Item",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )

            CategoryDropdown(
                selectedCategory = category,
                onCategorySelected = { category = it }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = price,
                onValueChange = { if (it.isDigitsOnly()) price = it }, //price = if (it.isEmpty()) "0" else it.filter { char -> char.isDigit() }
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = quantity,
                onValueChange = { if (it.isDigitsOnly()) quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onClose() }
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { onAdd(name, category, price.toDouble(), quantity.toInt()) },
                ) { Text( if(selectedItem == null) "Add" else "Edit" ) }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenPreview() {
    val mockState = ChecklistEditState(
        isDrawerOpen = false
    )

    ChecklistEditScreen(
        state = mockState,
        onEvent = {}
    )
}
