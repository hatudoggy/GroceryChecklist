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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilledTonalButton
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
import com.example.grocerychecklist.ui.component.AlertDialogExtend
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.BottomSheetChecklistItem
import com.example.grocerychecklist.ui.component.CategoryDropdown
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent


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

    BottomSheetChecklistItem(
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

    AlertDialogExtend(
        isOpen = state.isDeleteDialogOpen,
        onClose = { onEvent(ChecklistEditEvent.CloseDeleteDialog) },
        title = "Delete Item?",
        body = "Are you sure you want to delete this item?",
        actionButtons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        state.selectedItem.let {
                            if (it != null) {
                                onEvent(ChecklistEditEvent.DeleteChecklistItem(it.id))
                            }
                        }
                    },
                    colors = ButtonColors(
                        containerColor = ErrorTonal,
                        contentColor = ErrorText,
                        disabledContainerColor = ErrorTonal,
                        disabledContentColor = ErrorText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete Checklist Item")
                }
                FilledTonalButton(
                    onClick = {
                        state.selectedItem.let {
                            if (it != null) {
                                onEvent(ChecklistEditEvent.DeleteChecklistItemAndItem(it.id, it.itemId))
                            }
                        }
                    },
                    colors = ButtonColors(
                        containerColor = ErrorTonal,
                        contentColor = ErrorText,
                        disabledContainerColor = ErrorTonal,
                        disabledContentColor = ErrorText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete Checklist Item & Item")
                }
                TextButton(
                    onClick = { onEvent(ChecklistEditEvent.CloseDeleteDialog) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        },
    )

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
                        name = item.name,
                        variant = ChecklistItemComponentVariant.ChecklistItem,
                        category = item.category,
                        price = item.price,
                        quantity = item.quantity.toDouble(),
                        measurement = item.measurement,
                        onLongPress = { onEvent(ChecklistEditEvent.OpenActionMenu(item)) }
                    )
                }
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
