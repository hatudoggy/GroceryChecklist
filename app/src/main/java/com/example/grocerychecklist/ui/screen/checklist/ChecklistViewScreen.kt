package com.example.grocerychecklist.ui.screen.checklist

import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.R
import com.example.grocerychecklist.ui.component.ActionMenu
import com.example.grocerychecklist.ui.component.AlertDialogExtend
import com.example.grocerychecklist.ui.component.BottomSheetChecklistItem
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.ErrorComponent
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewUIState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistViewState
import java.time.LocalDateTime


data class ChecklistEditFormInputs (
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Int,
)

@Composable
fun ChecklistViewScreen(
    checklistName: String,
    state: ChecklistViewUIState,
    uiState: ChecklistViewState,
    onEvent: (ChecklistViewEvent) -> Unit,
)  {
    var shouldRefresh by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    BottomSheetChecklistItem(
        selectedItem = uiState.selectedItem,
        isOpen = uiState.isDrawerOpen,
        onClose = { onEvent(ChecklistViewEvent.CloseDrawer) },
        onAdd = { name, category, price, quantity ->
            if (uiState.selectedItem == null)
                onEvent(ChecklistViewEvent.AddChecklistItem(
                    ChecklistEditFormInputs(name, category, price, quantity)
                ))
            else
                onEvent(ChecklistViewEvent.EditChecklistItem(
                    uiState.selectedItem.id,
                    ChecklistEditFormInputs(name, category, price, quantity)
                ))
        },
        onVisible = { visible -> if(!visible) onEvent(ChecklistViewEvent.ClearSelectedItem)}
    )

    // Edit, Delete Action Menu
    ActionMenu(
        isOpen = uiState.isActionMenuOpen,
        onClose = { onEvent(ChecklistViewEvent.CloseActionMenu) },
        onEditMenu = {
            onEvent(ChecklistViewEvent.OpenDrawer)
        },
        onDeleteDialog = {
            onEvent(ChecklistViewEvent.OpenDeleteDialog)
        }
    )

    AlertDialogExtend(
        isOpen = uiState.isDeleteDialogOpen,
        onClose = { onEvent(ChecklistViewEvent.CloseDeleteDialog) },
        title = "Delete Item?",
        body = "Are you sure you want to delete this item?",
        actionButtons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        uiState.selectedItem.let {
                            if (it != null) {
                                onEvent(ChecklistViewEvent.DeleteChecklistItem(it.id))
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
                        uiState.selectedItem.let {
                            if (it != null) {
                                onEvent(ChecklistViewEvent.DeleteChecklistItemAndItem(it.id, it.itemId))
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
                    onClick = { onEvent(ChecklistViewEvent.CloseDeleteDialog) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        },
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onEvent(ChecklistViewEvent.OpenDrawer) },
                containerColor = PrimaryGreenSurface
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
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
                    checklistName,
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
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onEvent(ChecklistViewEvent.SetSearchQuery(it))
                }
            )
            Spacer(Modifier.height(8.dp))

            when (state) {
                is ChecklistViewUIState.Loading -> {
                    Text("Loading")
                }
                is ChecklistViewUIState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        ErrorComponent(errorMessage = R.string.checklist_error, onRetry = { shouldRefresh = true })
                    }
                }
                is ChecklistViewUIState.Success -> {

                    LazyColumn {
                        items(state.items) { item ->
                            ChecklistItemComponent(
                                name = item.name,
                                variant = ChecklistItemComponentVariant.ChecklistItem,
                                category = item.category,
                                price = item.price,
                                quantity = item.quantity.toDouble(),
                                measurement = item.measurement,
                                onLongPress = { onEvent(ChecklistViewEvent.OpenActionMenu(item)) }
                            )
                        }
                    }
                }

                ChecklistViewUIState.Empty -> TODO()
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenSuccessPreview() {
    val mockUIState = ChecklistViewState(
        isDrawerOpen = false
    )

    val mockState = ChecklistViewUIState.Success(
        items = listOf(
            ChecklistData(
                id = 1,
                itemId = 1,
                checklistId = 1,
                name = "Test Item",
                category = ItemCategory.SANITARY,
                price = 150.0,
                quantity = 2,
                measurement = Measurement.KILOGRAM,
                measurementValue = 5.0,
                photoRef = "",
                order = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isChecked = false,
            )
        )
    )

    ChecklistViewScreen(
        checklistName = "Test Checklist",
        state = mockState,
        uiState = mockUIState,
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenErrorPreview() {
    val mockUIState = ChecklistViewState(
        isDrawerOpen = false
    )

    val mockState = ChecklistViewUIState.Error

    ChecklistViewScreen(
        checklistName = "Test Checklist",
        state = mockState,
        uiState = mockUIState,
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenEmptyPreview() {
    val mockUIState = ChecklistViewState(
        isDrawerOpen = false
    )

    val mockState = ChecklistViewUIState.Empty

    ChecklistViewScreen(
        checklistName = "Test Checklist",
        state = mockState,
        uiState = mockUIState,
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenLoadingPreview() {
    val mockUIState = ChecklistViewState(
        isDrawerOpen = false
    )

    val mockState = ChecklistViewUIState.Loading

    ChecklistViewScreen(
        checklistName = "Test Checklist",
        state = mockState,
        uiState = mockUIState,
        onEvent = {}
    )
}


