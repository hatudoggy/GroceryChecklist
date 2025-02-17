package com.example.grocerychecklist.ui.screen.checklist

import ItemCategory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.ActionMenu
import com.example.grocerychecklist.ui.component.AlertDialogExtend
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.BottomSheetChecklistItem
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.ChipComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistData
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartState
import com.example.grocerychecklist.viewmodel.checklist.FilterType


data class ChecklistStartFormInputs (
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Int,
)

@Composable
fun ChecklistStartScreen(
    state: ChecklistStartState,
    onEvent: (ChecklistStartEvent) -> Unit,
) {
    BottomSheetChecklistItem(
        selectedItem = state.selectedItem,
        isOpen = state.isDrawerOpen,
        onClose = { onEvent(ChecklistStartEvent.CloseDrawer) },
        onAdd = { name, category, price, quantity ->
            if (state.selectedItem == null)
                onEvent(
                    ChecklistStartEvent.AddChecklistItem(
                    ChecklistStartFormInputs(name, category, price, quantity)
                ))
            else
                onEvent(
                    ChecklistStartEvent.EditChecklistItem(
                        state.selectedItem.id,
                        ChecklistStartFormInputs(name, category, price, quantity)
                ))
        },
        onVisible = { visible -> if(!visible) onEvent(ChecklistStartEvent.ClearSelectedItem)}
    )

    // Edit, Delete Action Menu
    ActionMenu(
        isOpen = state.isActionMenuOpen,
        onClose = { onEvent(ChecklistStartEvent.CloseActionMenu) },
        onEditMenu = {
            onEvent(ChecklistStartEvent.OpenDrawer)
        },
        onDeleteDialog = {
            onEvent(ChecklistStartEvent.OpenDeleteDialog)
        }
    )

    // Delete Dialog
    AlertDialogExtend(
        isOpen = state.isDeleteDialogOpen,
        onClose = { onEvent(ChecklistStartEvent.CloseDeleteDialog) },
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
                                onEvent(ChecklistStartEvent.DeleteChecklistItem(it.id))
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
                                onEvent(ChecklistStartEvent.DeleteChecklistItemAndItem(it.id, it.itemId))
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
                    onClick = { onEvent(ChecklistStartEvent.CloseDeleteDialog) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        },
    )

    BottomSheetCheckout(
        checkedItems = state.items.filter { it.isChecked },
        totalPrice = state.totalPrice,
        onCheckoutClick = { onEvent(ChecklistStartEvent.ProceedCheckout(state.items)) },
        isOpen = state.isCheckoutOpen,
        onClose = { onEvent(ChecklistStartEvent.CloseCheckout) },
    )

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = PrimaryGreenSurface,
                onClick = { onEvent(ChecklistStartEvent.OpenDrawer) },
                modifier = Modifier
                    .offset(y = (-78).dp),
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = { onEvent(ChecklistStartEvent.NavigateBack) }
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
                    "Main Grocery",
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
                            onEvent(ChecklistStartEvent.SelectChip(filterType))
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(state.filteredItems) { item ->
                    // Create and display a ChecklistItemComponent for each item in the filtered list.
                    ChecklistItemComponent(
                        name = item.name,
                        variant = ChecklistItemComponentVariant.ChecklistRadioItem,
                        category = item.category,
                        price = item.price,
                        quantity = item.quantity.toDouble(),
                        measurement = item.measurement,
                        isChecked = item.isChecked,
                        // When the checked state changes, update the ViewModel accordingly.
                        // Use onCheckedChange instead of onClick for handling the checkbox state.
                        onCheckedChange = { onEvent(ChecklistStartEvent.ToggleItemCheck(item)) },
                        onLongPress = { onEvent(ChecklistStartEvent.OpenActionMenu(item)) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                Modifier
                    .shadow(12.dp, RoundedCornerShape(15.dp))
                    .clickable { onEvent(ChecklistStartEvent.OpenCheckout) }
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
                            converter(Currency.PHP, state.totalPrice, false),
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



// Bottom sheet modal drawer for editing Checklist
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCheckout(
    checkedItems: List<ChecklistData>,
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
                    modifier = Modifier.weight(1f)
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
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset {
                        val offsetY = sheetState.requireOffset()
                        IntOffset(
                            x = 0,
                            y = -offsetY.toInt()
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
                        //color = Color.White
                    )
                    Text(
                        converter(Currency.PHP, totalPrice, false),
                        fontSize = 18.sp,
                        //color = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onCheckoutClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
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
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChecklistStartScreenPreview() {
    val mockState = ChecklistStartState(

    )

    ChecklistStartScreen(
        state = mockState,
        onEvent = {}
    )
}