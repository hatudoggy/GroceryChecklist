package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.R
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.ActionMenu
import com.example.grocerychecklist.ui.component.AlertDialogExtend
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.BottomSheetChecklistItem
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.ChipComponent
import com.example.grocerychecklist.ui.component.ErrorComponent
import com.example.grocerychecklist.ui.component.LoadingComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.*
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemFormInputs
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartUIState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartViewModel

enum class FilterType{
    ALL,
    CHECKED,
    UNCHECKED
}

@Composable
internal fun ChecklistStartScreen(
    viewModel: ChecklistStartViewModel,
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedChip by viewModel.selectedChip.collectAsState()
    val onEvent = viewModel::onEvent

    ChecklistStartScreen(
        state = state,
        uiState = uiState,
        searchQuery = searchQuery,
        selectedChip = selectedChip,

        loadItems = { onEvent(LoadData) },
        onSearch = { query -> onEvent(SearchQueryEvent(query)) },
        onSetSelectedChip = { filter -> onEvent(FilterSelection(filter)) },
        onSetSelectedItem = { item -> onEvent(ItemSelection(item)) },

        toggleDrawer = { onEvent(ToggleDrawer) },
        toggleDeleteDialog = { onEvent(ToggleDeleteDialog) },
        toggleActionMenu = { item -> onEvent(ToggleActionMenu(item)) },
        toggleItemCheck = { itemId -> onEvent(ToggleItemCheck(itemId)) },
        toggleCheckout = { onEvent(ToggleCheckout) },
        onCheckoutClick = { onEvent(ProceedCheckout) },

        onAddChecklistItem = { formInputs -> onEvent(ItemAddition(formInputs)) },
        onEditChecklistItem = { id, formInputs -> onEvent(ItemModification(id, formInputs)) },
        onDeleteChecklistItem = { id, itemId -> onEvent(ItemDeletion(id, itemId)) },
        onNavigateBack = { onEvent(NavigateBack) },
    )
}

@Composable
internal fun ChecklistStartScreen(
    state: ChecklistStartState,
    uiState: ChecklistStartUIState,
    searchQuery: String,
    selectedChip: FilterType,

    loadItems: () -> Unit,
    onSearch: (String) -> Unit,
    onSetSelectedChip: (FilterType) -> Unit,
    onSetSelectedItem: (ChecklistItemData?) -> Unit,

    toggleDrawer: () -> Unit,
    toggleDeleteDialog: () -> Unit,
    toggleActionMenu: (ChecklistItemData) -> Unit,
    toggleItemCheck: (Long) -> Unit,
    toggleCheckout: () -> Unit,
    onCheckoutClick: () -> Unit,

    onAddChecklistItem: (ChecklistItemFormInputs) -> Unit,
    onEditChecklistItem: (Long, ChecklistItemFormInputs) -> Unit,
    onDeleteChecklistItem: (Long, Long?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val checkedItems = if (uiState is ChecklistStartUIState.Success) {
        uiState.checklists.filter { it.isChecked }
    } else {
        emptyList()
    }

    BottomSheetChecklistItem(
        selectedItem = state.selectedItem,
        isOpen = state.isDrawerOpen,
        onClose = { toggleDrawer },
        onAdd = { name, category, price, quantity ->
            if (state.selectedItem == null)
                onAddChecklistItem(
                    ChecklistItemFormInputs(name, category, price, quantity)
                )
            else {
                onEditChecklistItem(
                    state.selectedItem.id,
                    ChecklistItemFormInputs(name, category, price, quantity)
                )
            }
        },
        onVisible = { visible -> if(!visible) onSetSelectedItem(null) }
    )

    // Edit, Delete Action Menu
    ActionMenu(
        isOpen = state.isActionMenuOpen,
        onClose = { toggleActionMenu(state.selectedItem!!) },
        onEditMenu = {
            toggleDrawer
        },
        onDeleteDialog = {
            toggleDeleteDialog
        }
    )

    // Delete Dialog
    AlertDialogExtend(
        isOpen = state.isDeleteDialogOpen,
        onClose = { toggleDeleteDialog },
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
                                onDeleteChecklistItem(it.id, null)
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
                                onDeleteChecklistItem(it.id, it.itemId)
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
                    onClick = { toggleDeleteDialog },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        },
    )

    BottomSheetCheckout(
        checkedItems = checkedItems,
        totalPrice = checkedItems.sumOf { it.price * it.quantity },
        onCheckoutClick = { onCheckoutClick },
        isOpen = state.isCheckoutOpen,
        onClose = { toggleCheckout },
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = PrimaryGreenSurface,
                onClick = { toggleDrawer },
                modifier = Modifier
                    .offset(y = (-78).dp),
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = { onNavigateBack }
            )
        },

        ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    state.checklistName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        modifier = Modifier
                            .size(18.dp),
                        tint = Color.LightGray,
                        contentDescription = "eye"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Shopping Mode",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Iterates through each FilterType to create a ChipComponent.
                FilterType.entries.forEach { filterType ->
                    ChipComponent(
                        label = filterType.name.lowercase().replaceFirstChar { it.uppercase() },
                        isActive = state.selectedChip == filterType,
                        onClick = {
                            onSetSelectedChip(filterType)
                        }
                    )
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
                        loadItems = loadItems
                    )
                }
                is ChecklistStartUIState.Success -> {
                    ChecklistSuccessUI(
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        toggleItemCheck = toggleItemCheck,
                        toggleActionMenu = toggleActionMenu,
                        onSetSelectedItem = onSetSelectedItem
                    )
                }

                ChecklistStartUIState.Empty -> {
                    ChecklistEmptyUI()
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier
                    .shadow(12.dp, RoundedCornerShape(15.dp))
                    .clickable { toggleCheckout }
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 16.dp)
                        .weight(1f, fill = true),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val converter = ConvertNumToCurrency()
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 150.dp),
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

@Composable
private fun ChecklistSuccessUI(
    uiState: ChecklistStartUIState.Success,
    toggleItemCheck: (Long) -> Unit,
    toggleActionMenu: (ChecklistItemData) -> Unit,
    onSetSelectedItem: (ChecklistItemData) -> Unit,
    modifier: Modifier = Modifier
){
    val checklistItems = uiState.checklists
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = checklistItems,
            itemContent = { item ->
                ChecklistItemComponent(
                    name = item.name,
                    variant = ChecklistItemComponentVariant.ChecklistRadioItem,
                    category = item.category,
                    price = item.price,
                    quantity = item.quantity.toDouble(),
                    measurement = item.measurement,
                    isChecked = item.isChecked,
                    onCheckedChange = { toggleItemCheck(item.id) },
                    onLongPress = {
                        onSetSelectedItem(item)
                        toggleActionMenu(item)
                    }
                )
            }
        )
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


// Bottom sheet modal drawer for editing Checklist
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCheckout(
    checkedItems: List<ChecklistItemData>,
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    isOpen: Boolean,
    onClose: () -> Unit,
    onVisible: (Boolean) -> Unit = {}
) {

    val sheetState = rememberModalBottomSheetState()


    BottomSheet(
        isOpen = isOpen,
        onClose = onClose,
        skipExpand = false,
        onVisibilityChanged = { visible -> onVisible(visible) },
        sheetState = sheetState,
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.BottomStart
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(
                        start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
                    )
            ) {
                Text(
                    "Checkout",
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    items(checkedItems) { item ->
                        ChecklistItemComponent(
                            name = item.name,
                            variant = ChecklistItemComponentVariant.ChecklistItem,
                            category = item.category,
                            price = item.price,
                            quantity = item.quantity.toDouble(),
                            measurement = item.measurement,
                            isChecked = item.isChecked,
                        )
                    }
                }

                Spacer(Modifier.height(100.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset {
                        val offsetY = sheetState.requireOffset()
                        IntOffset(
                            x = 0,
                            y = -offsetY.toInt() + 100
                        )
                    }
                    .background(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    val converter = ConvertNumToCurrency()
                    Text(
                        "Total",
                        fontSize = 16.sp,
                    )
                    Text(
                        converter(Currency.PHP, totalPrice, false),
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onCheckoutClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(5.dp),
                    ) {
                        Text(
                            "Checkout",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            Icons.Default.ShoppingCartCheckout,
                            contentDescription = "Checkout",
                            tint = Color.White
                        )
                    }

                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistBaseScreenPreview() {

}