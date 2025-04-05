package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.R
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.AlertDialogExtend
import com.example.grocerychecklist.ui.component.BottomSheetChecklistItem
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.ChipComponent
import com.example.grocerychecklist.ui.component.ErrorComponent
import com.example.grocerychecklist.ui.component.LoadingComponent
import com.example.grocerychecklist.ui.screen.checklist.components.BottomSheetCheckout
import com.example.grocerychecklist.ui.screen.checklist.components.ChecklistFAB
import com.example.grocerychecklist.ui.screen.checklist.components.ChecklistToolbar
import com.example.grocerychecklist.ui.screen.checklist.components.CopyChecklistDialogComponent
import com.example.grocerychecklist.ui.screen.checklist.components.FilterSortBottomSheet
import com.example.grocerychecklist.ui.screen.checklist.components.SelectionBottomBar
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemFormInputs
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.*
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartUIState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartViewModel

/**
 * Enum class representing different filter types for a list of items.
 *
 * The filter types allow users to view:
 *  - ALL: All items in the list, regardless of their checked state.
 *  - CHECKED: Only items that are currently checked.
 *  - UNCHECKED: Only items that are currently unchecked.
 */
enum class FilterType{
    ALL, CHECKED, UNCHECKED
}

/**
 * Enum class representing the different modes for a checklist.
 *
 *  - EDIT:  Indicates the checklist is in edit mode, allowing users to modify the items.
 *  - SHOPPING: Indicates the checklist is in shopping mode, allowing users to check off items as they acquire them.
 */
enum class ChecklistMode{
    EDIT, SHOPPING
}


/**
 * Composable function for displaying the checklist start screen.  This screen acts as a
 * coordinator, collecting state from the [ChecklistStartViewModel] and passing it down
 * to the actual UI composable, also named `ChecklistStartScreen`, along with event
 * handlers to be triggered by user interactions within the UI.
 *
 * @param viewModel The [ChecklistStartViewModel] providing the data and logic for the screen.
 */
@Composable
internal fun ChecklistStartScreen(
    viewModel: ChecklistStartViewModel,
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    ChecklistStartScreen(
        state = state,
        uiState = uiState,
        searchQuery = searchQuery,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChecklistStartScreen(
    state: ChecklistStartState,
    uiState: ChecklistStartUIState,
    searchQuery: String,
    onEvent: (ChecklistStartEvent) -> Unit
) {
    // Delete Dialog
    AlertDialogExtend(
        isOpen = state.isDeleteDialogOpen,
        onClose = { onEvent(ToggleDeleteDialog) },
        title = "Delete Item?",
        body = "Are you sure you want to delete this item?",
        actionButtons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        state.editingItem.let {
                            if (it != null) {
                                onEvent(ItemDeletion(it.id, null))
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
                        state.editingItem.let {
                            if (it != null) {
                                onEvent(ItemDeletion(it.id, it.itemId))
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
                    Text("Delete Checklist Item & Grocery Item")
                }
                TextButton(
                    onClick = { onEvent(ToggleDeleteDialog) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = DarkGray)
                }
            }
        },
    )

    if (state.mode == ChecklistMode.SHOPPING){
        BottomSheetCheckout(
            checkedItems = state.checkedItems,
            totalPrice =  state.checkedItems.sumOf { it.price * it.quantity },
            onCheckoutClick = {  onEvent(ProceedCheckout) },
            isOpen = state.isCheckoutOpen,
            onClose = {  onEvent(ToggleCheckout) },
        )
    }

    BottomSheetChecklistItem(
        selectedItem = state.editingItem,
        isOpen = state.isDrawerOpen,
        onClose = { onEvent(ToggleDrawer) },
        onAdd = { name, category, price, quantity ->
            if (state.editingItem == null)
                onEvent(ItemAddition(ChecklistItemFormInputs(name, category.name, price, quantity)))
            else {
                onEvent(ItemModification(state.editingItem.id, ChecklistItemFormInputs(name, category.name, price, quantity)))
            }
        },
        onVisible = { visible -> if (!visible) onEvent(ClearSelection) }
    )

    if (state.isFilterBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ToggleFilterBottomSheet) }
        ) {
            FilterSortBottomSheet(
                state = state,
                onEvent = onEvent
            )
        }
    }

    if (state.isCopyDialogOpen) {
        CopyChecklistDialogComponent(
            state = state,
            onDismiss = { onEvent(ToggleCopyDialog) },
            onConfirm = { onEvent(CopyToNewChecklist(it)) },
            onSetNewChecklist = { onEvent(SetNewChecklist(it)) },
            toggleIconPicker = { onEvent(ToggleIconPicker) },
            modifier = Modifier
        )
    }

    Scaffold(
        topBar = {
            ChecklistToolbar(
                title = state.checklistName,
                searchQuery = searchQuery,
                onSearchChange = { onEvent(SearchQueryEvent(it)) },
                onToggleDrawer = { onEvent(ToggleDrawer) },
                onNavigateBack = { onEvent(NavigateBack) },
                selectedCount = state.selectedItems.size,
                onSelectAll = {
                    if (state.selectedItems.isEmpty()) {
                        onEvent(SelectAllItems)
                    } else {
                        onEvent(ClearSelection)
                    } },
                onCancelSelection = {
                    onEvent(ClearSelection)
                    onEvent(ChangeItemSelectMode(mode = false))
                },
                hasSelectedAll = uiState is ChecklistStartUIState.Success && uiState.checklists.size == state.selectedItems.size,
                isSelectionModeActive = state.isSelectionModeActive,
                onToggleFilterBottomSheet = { onEvent(ToggleFilterBottomSheet) }
            )
        },
        bottomBar = {
            if (state.isSelectionModeActive) {
                SelectionBottomBar(
                    selectedCount = state.selectedItems.size,
                    onDelete = { onEvent(ToggleDeleteDialog) },
                    onEditItem = { onEvent(ToggleDrawer) },
                    onMoreActions = { onEvent(ToggleMoreActionsMenu) },
                    onCopyToNewChecklist = { onEvent(ToggleCopyDialog) },
                )
            }
        },
        floatingActionButton = {
            ChecklistFAB(
                currentMode = state.mode,
                hasItems = uiState is ChecklistStartUIState.Success && uiState.checklists.isNotEmpty(),
                hasSelectedItems = state.selectedItems.isNotEmpty(),
                onAddItem = { onEvent(ToggleDrawer) },
                onToggleMode = {
                    onEvent(
                        ChangeMode(
                            if (state.mode == ChecklistMode.SHOPPING) ChecklistMode.EDIT
                            else ChecklistMode.SHOPPING
                        )
                    )
                },
                onDeleteSelected = { onEvent(ToggleDeleteDialog) },
                paddingBottom = 80.dp
            )
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(10.dp)
        ) {

//            Row (
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    state.checklistName,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Filled.ShoppingCart,
//                        modifier = Modifier
//                            .size(18.dp),
//                        tint = Color.LightGray,
//                        contentDescription = "eye"
//                    )
//                    Spacer(Modifier.width(4.dp))
//                    Text(
//                        "Shopping Mode",
//                        fontSize = 14.sp,
//                        color = Color.LightGray
//                    )
//                }
//            }
//            Spacer(Modifier.height(16.dp))

            // Only show filter chips in shopping mode
            if (state.mode == ChecklistMode.SHOPPING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Iterates through each FilterType to create a ChipComponent.
                    FilterType.entries.forEach { filterType ->
                        ChipComponent(
                            label = filterType.name.lowercase().replaceFirstChar { it.uppercase() },
                            isActive = state.selectedChip == filterType,
                            onClick = { onEvent(FilterSelection(filterType))}
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            when(uiState) {
                ChecklistStartUIState.Loading -> {
                    ChecklistLoadingUI(modifier = Modifier.weight(1f))
                }
                is ChecklistStartUIState.Error -> {
                    ChecklistErrorUI(
                        modifier = Modifier.weight(1f),
                        errorMessage = uiState.message,
                        loadItems = { onEvent(LoadData) }
                    )
                }
                is ChecklistStartUIState.Success -> {
                    ChecklistSuccessUI(state = state, uiState = uiState, onEvent = onEvent, modifier = Modifier.weight(1f))
                }

                is ChecklistStartUIState.Empty -> {
                    ChecklistEmptyUI()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.mode == ChecklistMode.SHOPPING) {
                CheckoutButton(
                    checkedItems = state.checkedItems,
                    toggleCheckout = { onEvent(ToggleCheckout) }
                )
            }
        }
    }
}

@Composable
private fun CheckoutButton(
    checkedItems: List<ChecklistItemData> = emptyList(),
    toggleCheckout: () -> Unit = {},
) {
    val converter = ConvertNumToCurrency()

    Row(
        modifier = Modifier
            .shadow(12.dp, RoundedCornerShape(15.dp))
            .clickable { toggleCheckout() }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
                .weight(1f, fill = true),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total",
                fontSize = 16.sp,
                color = Color.White
            )
            Row {
                Text(
                    converter(Currency.PHP, checkedItems.sumOf { it.price * it.quantity }, false),
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Checkout",
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ChecklistLoadingUI(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        LoadingComponent(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            loadingMessage = R.string.checklist_loading
        )
    }
}

@Composable
private fun ChecklistEmptyUI(
    modifier: Modifier = Modifier,
    searchQuery: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Checklist,
                contentDescription = "Empty Checklist",
                modifier = Modifier.size(48.dp),
                tint = DarkGray.copy(alpha = 0.6f)
            )
            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "No items match your search"
                } else {
                    "No Checklist Items"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray.copy(alpha = 0.6f)
            )
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Try a different search term",
                    fontSize = 14.sp,
                    color = DarkGray.copy(alpha = 0.6f)
                )
            }
    }
}

@Composable
private fun ChecklistSuccessUI(
    state: ChecklistStartState,
    uiState: ChecklistStartUIState.Success,
    onEvent: (ChecklistStartEvent) -> Unit,
    modifier: Modifier = Modifier
){
    val filteredItems = when (state.selectedChip) {
        FilterType.ALL -> uiState.checklists
        FilterType.CHECKED -> state.checkedItems
        FilterType.UNCHECKED -> uiState.checklists.minus(state.checkedItems)
    }

    val sortedItems = when (state.selectedSortOption) {
        ChecklistItemOrder.Name -> filteredItems.sortedBy { it.name }
        ChecklistItemOrder.Category -> filteredItems.sortedBy { it.category }
        ChecklistItemOrder.Price -> filteredItems.sortedBy { it.price }
        ChecklistItemOrder.Quantity -> filteredItems.sortedBy { it.quantity }
        ChecklistItemOrder.Order -> filteredItems
        ChecklistItemOrder.Date -> filteredItems.sortedBy { it.createdAt }
    }.let { if (!state.isSortAscending) it.reversed() else it }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp),
    ) {

        sortedItems.groupBy { it.category }.forEach { (_, items) ->
            items(items) { item ->
                ChecklistItemComponent(
                    name = item.name,
                    onSelectionChange = {
                        if (state.mode == ChecklistMode.EDIT) {
                            onEvent(ToggleItemSelection(item.id))
                        }
                    },
                    onLongPress = {
                        if (state.mode == ChecklistMode.EDIT) {
                            onEvent(ToggleItemSelection(item.id))
                        }
                    },
                    onCheckedChange = {
                        if (state.mode == ChecklistMode.SHOPPING) {
                            onEvent(ToggleItemCheck(item))
                        }
                    },
                    variant = if ( state.mode == ChecklistMode.SHOPPING) {
                        ChecklistItemComponentVariant.ChecklistRadioItem
                    }   else {
                        ChecklistItemComponentVariant.ChecklistItem
                    },
                    category = item.category,
                    price = item.price,
                    quantity = item.quantity.toDouble(),
                    measurement = item.measurement,
                    isChecked = state.checkedItems.any { it.id == item.id },
                    isSelected = state.selectedItems.contains(item.id)
                )
            }
        }
    }
}

@Composable
private fun ChecklistErrorUI(
    errorMessage: String?,
    loadItems: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultErrorMessage = stringResource(id = R.string.checklist_error)
    Box(modifier = modifier
        .fillMaxSize()
        .padding(bottom = 150.dp),
        contentAlignment = Alignment.Center
    ) {
        ErrorComponent(errorMessage = errorMessage?: defaultErrorMessage, onRetry = loadItems)
    }
}




