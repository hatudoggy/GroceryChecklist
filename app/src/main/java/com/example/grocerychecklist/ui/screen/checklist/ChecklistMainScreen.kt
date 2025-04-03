package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grocerychecklist.R
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.ui.component.ActionMenu
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.ButtonCardIconComponent
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.component.ErrorComponent
import com.example.grocerychecklist.ui.component.LoadingComponent
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent.*
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainUIState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun ChecklistMainScreen(
    viewModel: ChecklistMainViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    ChecklistMainScreen(
        // States
        state = state,
        uiState = uiState,
        searchQuery = searchQuery,

        //General (Common Checklist) Events
        loadChecklist = { onEvent(LoadChecklists) },
        onSearch = { onEvent(SearchQueryEvent(it)) },
        toggleDrawer = { onEvent(ToggleDrawer) },
        toggleDeleteDialog = { onEvent(ToggleDeleteDialog) },
        toggleActionMenu = { checklist -> onEvent(ToggleActionMenu(checklist)) },
        onAddChecklist = { onEvent(AddChecklist(it)) },
        onUpdateChecklist = { checklist -> onEvent(UpdateChecklist(checklist)) },
        onDeleteChecklist = { checklist -> onEvent(DeleteChecklist(checklist)) },

        //UI Events
        toggleIconPicker = { onEvent(ToggleIconPicker) },
        onSetEditingChecklist = { onEvent(SetEditingChecklist(it)) },
        onSetNewChecklist = { onEvent(SetNewChecklist(it)) },
        onNavigateChecklist = { id, name -> onEvent(NavigateChecklist(id, name)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChecklistMainScreen(
    state: ChecklistMainState,
    uiState: ChecklistMainUIState,
    searchQuery: String,

    loadChecklist: () -> Unit,
    onSearch: (String) -> Unit,
    toggleDrawer: () -> Unit,
    toggleDeleteDialog: () -> Unit,
    toggleActionMenu: (Checklist) -> Unit,
    onAddChecklist: (ChecklistInput) -> Unit,
    onUpdateChecklist: (Checklist) -> Unit,
    onDeleteChecklist: (Checklist) -> Unit,

    toggleIconPicker: () -> Unit,
    onSetEditingChecklist: (Checklist) -> Unit,
    onSetNewChecklist: (ChecklistInput) -> Unit,
    onNavigateChecklist: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Create Checklist Bottom Sheet
    BottomSheetChecklist(
        isOpen = state.isDrawerOpen,
        onClose = toggleDrawer,
        state = state,
        toggleIconPicker = toggleIconPicker,
        toggleDrawer = toggleDrawer,
        onSetNewChecklist = onSetNewChecklist,
        onSetEditingChecklist = onSetEditingChecklist,
        onAddChecklist = onAddChecklist,
        onUpdateChecklist = onUpdateChecklist,
    )

    DialogModal(
        isOpen = state.isIconPickerOpen,
        onClose = toggleIconPicker
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ChecklistCategory.entries.size) { index ->
                    val category = ChecklistCategory.entries[index]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (state.selectedChecklist != null && state.editingChecklist != null) {
                                    SetEditingChecklist(
                                        state.editingChecklist.copy(
                                            icon = IconOption.fromName(category.text)
                                                ?: IconOption.MAIN_GROCERY,
                                            iconBackgroundColor = ColorOption.fromColor(category.color)
                                                ?: ColorOption.CopySkyGreen
                                        )
                                    )
                                } else
                                    SetNewChecklist(
                                        state.newChecklist.copy(
                                            icon = IconOption.fromName(category.text)
                                                ?: IconOption.MAIN_GROCERY,
                                            iconBackgroundColor = ColorOption.fromColor(category.color)
                                                ?: ColorOption.CopySkyGreen
                                        )
                                    )
                                toggleIconPicker
                            }
                    ) {
                        ButtonCardIconComponent(
                            backgroundColor = category.color,
                            icon = category.icon,
                            wrapperSize = 56.dp,
                            iconSize = 30.dp
                        )
                    }
                }
            }
        }
    }

    // Edit, Delete Action Menu
    if (state.selectedChecklist != null){
        ActionMenu(
            isOpen = state.isActionMenuOpen,
            onClose = { toggleActionMenu(state.selectedChecklist) },
            onEditMenu = {
                toggleDrawer
            },
            onDeleteDialog = {
                toggleDeleteDialog
            }
        )

        // Delete Dialog
        if (state.isDeleteDialogOpen) {
            AlertDialog(
                onDismissRequest = { toggleDeleteDialog },
                title = { Text("Delete Item?") },
                text = { Text("Are you sure you want to delete this item?") },
                confirmButton = {
                    TextButton(onClick = { onDeleteChecklist(state.selectedChecklist) }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = toggleDeleteDialog) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            )
        }
    }

    // Main screen content
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = toggleDrawer,
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
                .padding(8.dp)
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
                placeholderText = "Search",
                value = searchQuery,
                onValueChange = { onSearch(it) }
            )
            Spacer(Modifier.height(8.dp))

            when (uiState) {
                is ChecklistMainUIState.Success -> {
                    ChecklistMainSuccessUI(
                        uiState = uiState,
                        onNavigateChecklist = onNavigateChecklist,
                        toggleActionMenu = toggleActionMenu,
                        modifier = Modifier.weight(1f)
                    )
                }
                is ChecklistMainUIState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = "Empty Checklist",
                                modifier = Modifier.size(32.dp),
                            )
                            Text(
                                text = "No Checklist Items",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }

                    }
                }

                is ChecklistMainUIState.Error -> {
                    val errorMessage = uiState.message
                    val defaultErrorMessage = stringResource(R.string.checklist_error)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ErrorComponent(
                            errorMessage = errorMessage ?: defaultErrorMessage,
                            onRetry = { loadChecklist }
                        )
                    }
                }
                is ChecklistMainUIState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoadingComponent(loadingMessage = R.string.checklist_loading)
                    }
                }
            }
        }
    }
}

// Bottom sheet modal drawer for editing Checklist
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetChecklist(
    isOpen: Boolean,
    onClose: () -> Unit,
    toggleIconPicker: () -> Unit,
    toggleDrawer: () -> Unit,
    onSetEditingChecklist: (Checklist) -> Unit,
    onSetNewChecklist: (ChecklistInput) -> Unit,
    onAddChecklist: (ChecklistInput) -> Unit,
    onUpdateChecklist: (Checklist) -> Unit,
    state: ChecklistMainState,
) {
    BottomSheet(
        isOpen = isOpen,
        onClose = onClose,
        skipExpand = true,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
                )
        ) {
            Text(
                (if (state.editingChecklist != null) "Edit" else "Create a") + " Checklist",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { toggleIconPicker }
                ) {
                    ButtonCardIconComponent(
                        backgroundColor = state.editingChecklist?.iconBackgroundColor?.color
                            ?: state.newChecklist.iconBackgroundColor.color,
                        icon = state.editingChecklist?.icon?.imageVector
                            ?: state.newChecklist.icon.imageVector,
                        wrapperSize = 56.dp,
                        iconSize = 30.dp
                    )
                }

                // Name of Checklist
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.editingChecklist?.name ?: state.newChecklist.name,
                    onValueChange = { e ->
                        if (state.editingChecklist != null)
                            onSetEditingChecklist(state.editingChecklist.copy(name = e))
                        else
                            onSetNewChecklist(state.newChecklist.copy(name = e))
                    },
                    label = { Text("Name") }
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 5,
                value = state.editingChecklist?.description ?: state.newChecklist.description,
                onValueChange = { e ->
                    if (state.editingChecklist != null)
                        onSetEditingChecklist(state.editingChecklist.copy(description = e))
                    else
                        onSetNewChecklist(state.newChecklist.copy(description = e))
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { toggleDrawer }
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    enabled = state.editingChecklist?.name?.trim()
                        ?.isNotEmpty() == true || state.newChecklist.name.trim().isNotEmpty(),
                    onClick = {
                        if (state.editingChecklist != null)
                            onUpdateChecklist(state.editingChecklist)
                        else
                            onAddChecklist(state.newChecklist)

                    },
                ) { if (state.editingChecklist != null) Text("Edit") else Text("Add") }
            }
        }
    }
}


// Dialog for icon picker
@Composable
fun DialogModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    dialogContent: @Composable () -> Unit
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = onClose,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                dialogContent()
            }
        }
    }
}

@Composable
private fun ChecklistMainSuccessUI(
    uiState: ChecklistMainUIState.Success,
    onNavigateChecklist: (Long, String) -> Unit,
    toggleActionMenu: (Checklist) -> Unit,
    modifier: Modifier = Modifier
) {
    val checklists = uiState.data
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = checklists,
            itemContent = { item ->
                ButtonCardComponent(
                    name = item.name,
                    description = item.description.ifBlank { "No Description" },
                    date = item.updatedAt?.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                        .toString(),
                    icon = item.icon.imageVector,
                    iconBackgroundColor = item.iconBackgroundColor.color,
                    variant = ButtonCardComponentVariant.Checklist,
                    onClick = { onNavigateChecklist(item.id, item.name) },
                    onLongPress = { toggleActionMenu(item) }
                )
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreviewEmpty() {
    ChecklistMainScreen(
        state = ChecklistMainState(),
        uiState = ChecklistMainUIState.Empty,
        searchQuery = "",
        loadChecklist = {},
        onSearch = {},
        toggleDrawer = {},
        toggleIconPicker = {},
        toggleDeleteDialog = {},
        toggleActionMenu = {},
        onAddChecklist = {},
        onUpdateChecklist = {},
        onDeleteChecklist = {},
        onSetEditingChecklist = {},
        onSetNewChecklist = {},
        onNavigateChecklist = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreviewLoading() {
    ChecklistMainScreen(
        state = ChecklistMainState(),
        uiState = ChecklistMainUIState.Loading,
        searchQuery = "",
        loadChecklist = {},
        onSearch = {},
        toggleDrawer = {},
        toggleIconPicker = {},
        toggleDeleteDialog = {},
        toggleActionMenu = {},
        onAddChecklist = {},
        onUpdateChecklist = {},
        onDeleteChecklist = {},
        onSetEditingChecklist = {},
        onSetNewChecklist = {},
        onNavigateChecklist = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreviewError() {
    ChecklistMainScreen(
        state = ChecklistMainState(),
        uiState = ChecklistMainUIState.Error(
            "Failed to load checklists"
        ),
        searchQuery = "",
        loadChecklist = {},
        onSearch = {},
        toggleDrawer = {},
        toggleIconPicker = {},
        toggleDeleteDialog = {},
        toggleActionMenu = {},
        onAddChecklist = {},
        onUpdateChecklist = {},
        onDeleteChecklist = {},
        onSetEditingChecklist = {},
        onSetNewChecklist = {},
        onNavigateChecklist = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreviewSuccess() {
    ChecklistMainScreen(
        state = ChecklistMainState(),
        uiState = ChecklistMainUIState.Success(
            data = listOf(
                Checklist(
                    id = 1,
                    name = "Test Checklist",
                    description = "This is a test checklist",
                    icon = IconOption.MAIN_GROCERY,
                    iconBackgroundColor = ColorOption.CopySkyGreen,
                    createdAt = LocalDateTime.now(),
                    updatedAt =  LocalDateTime.now(),
                    lastOpenedAt =  LocalDateTime.now(),
                    lastShopAt =  LocalDateTime.now()
                )
            )
        ),
        searchQuery = "",
        loadChecklist = {},
        onSearch = {},
        toggleDrawer = {},
        toggleIconPicker = {},
        toggleDeleteDialog = {},
        toggleActionMenu = {},
        onAddChecklist = {},
        onUpdateChecklist = {},
        onDeleteChecklist = {},
        onSetEditingChecklist = {},
        onSetNewChecklist = {},
        onNavigateChecklist = { _, _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState()
    var text by remember { mutableStateOf("Hello") }

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 12.dp, bottom = 8.dp,
                )
        ) {
            Text(
                "Create a Checklist",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ButtonCardIconComponent(
                    backgroundColor = ChecklistCategory.MAIN_GROCERY.color,
                    icon = ChecklistCategory.MAIN_GROCERY.icon,
                    wrapperSize = 56.dp,
                    iconSize = 30.dp
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Name") }
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 5,
                value = text,
                onValueChange = { text = it },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {}
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {},
                ) { Text("Add") }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = false)
@Composable
fun BottomSheetActionsPreview() {
    val sheetState = rememberStandardBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp, 5.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {}
                    .padding(10.dp)
            ) {
                Text(
                    text = "Edit",
                    fontSize = 18.sp,
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp, 5.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {}
                    .padding(10.dp)
            ) {
                Text(
                    text = "Delete",
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun IconPickerPreview() {

    Dialog(
        onDismissRequest = {},
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ChecklistCategory.entries.size) { index ->
                        val category = ChecklistCategory.entries[index]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {

                                }
                        ) {
                            ButtonCardIconComponent(
                                backgroundColor = category.color,
                                icon = category.icon,
                                wrapperSize = 56.dp,
                                iconSize = 30.dp
                            )
                        }
                    }
                }
//                HorizontalDivider()
//                LazyRow(
//                    //modifier = Modifier.weight(1f)
//                ) {
//                    items(5) {
//                        Icon(
//                            Icons.Filled.Circle,
//                            contentDescription = "color",
//                            tint = Color.Gray,
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//                }
            }
        }
    }
}