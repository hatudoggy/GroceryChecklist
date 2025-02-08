package com.example.grocerychecklist.ui.screen.checklist

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.FullHeightDialogComponent
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistEditViewModel

data class ChecklistEditDialogInputs (
    val name: String,
    val updateName: (value: String) -> Unit,
    val category: String,
    val updateCategory: (value: String) -> Unit,
    val price: String,
    val updatePrice: (value: String) -> Unit,
    val quantity: String,
    val updateQuantity: (value: String) -> Unit,
)

@Composable
fun ChecklistEditScreen(
    state: ChecklistEditState,
    viewModel: ChecklistEditViewModel,
    onEvent: (ChecklistEditEvent) -> Unit,
)  {
    val filteredItems by viewModel.filteredItems.collectAsState(emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    val dialogInputs = ChecklistEditDialogInputs(
        name = state.itemName,
        updateName = { name -> onEvent(ChecklistEditEvent.SetItemName(name)) },
        category = state.itemCategory,
        updateCategory = { category -> onEvent(ChecklistEditEvent.SetItemCategory(category)) },
        price = state.itemPrice,
        updatePrice = { price -> onEvent(ChecklistEditEvent.SetItemPrice(price)) },
        quantity = state.itemQuantity,
        updateQuantity = { quantity -> onEvent(ChecklistEditEvent.SetItemQuantity(quantity)) },
    )

    if (state.isAddingChecklistItem) {
        ChecklistItemDialogComponent(
            onDismissRequest = { onEvent(ChecklistEditEvent.CloseDialog) },
            dialogInputs = dialogInputs
        )
    }

    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onEvent(ChecklistEditEvent.OpenDialog) },
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
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) }
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(filteredItems) { item ->
                    ChecklistItemComponent(
                        name = item.name,
                        variant = ChecklistItemComponentVariant.ChecklistItem,
                        category = item.category,
                        price = item.price,
                        quantity = item.quantity,
                        measurement = item.measurement,
                    )
                }
            }
        }
    }
//    FullHeightDialogComponent({}, scaffoldTopBar = {
//        ChecklistDialogTopBarComponent(onDismissRequest = {})
//    }, scaffoldContent = { innerPadding ->
//        ChecklistDialogContentComponent(innerPadding)
//    })
}

@Composable
fun ChecklistItemDialogComponent(
    onDismissRequest: () -> Unit,
    dialogInputs: ChecklistEditDialogInputs
) {

    FullHeightDialogComponent(onDismissRequest, scaffoldTopBar = {
        ChecklistDialogTopBarComponent(onDismissRequest)
    }, scaffoldContent = { innerPadding ->
        ChecklistDialogContentComponent(
            innerPadding,
            dialogInputs = dialogInputs
        )
    })
}

@Composable
fun ChecklistDialogTopBarComponent(
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(18.dp, 28.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onDismissRequest() }
            )
            {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.04f))
            Text(
                text = "Add Checklist Item",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
            )
        }
        TextButton(onClick = {}) {
            Text(
                "Save", style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
fun ChecklistDialogContentComponent(
    innerPadding: PaddingValues,
    dialogInputs: ChecklistEditDialogInputs
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
            value = dialogInputs.name,
            onValueChange = { it -> dialogInputs.updateName(it) },
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
            value = dialogInputs.category,
            onValueChange = { it -> dialogInputs.updateCategory(it) },
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
            value = dialogInputs.price,
            onValueChange = { it -> dialogInputs.updatePrice(it) },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Quantity", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = dialogInputs.quantity,
            onValueChange = { it -> dialogInputs.updateQuantity(it) },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenPreview() {
    val mockState = ChecklistEditState(
        isAddingChecklistItem = false,
        itemName = ""
    )

    ChecklistEditScreen(
        state = mockState,
        viewModel = ChecklistEditViewModel(Navigator()),
        onEvent = {}
    )
}
