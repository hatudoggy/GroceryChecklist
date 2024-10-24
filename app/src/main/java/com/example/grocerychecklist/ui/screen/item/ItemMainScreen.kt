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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
            //dialogInputs = dialogInputs
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
                items(5) {
                    ChecklistItemComponent(
                        name = "Tender Juicy Hot dog",
                        variant = ChecklistItemComponentVariant.Item,
                        category = ItemCategory.MEAT,
                        price = 250.00,
                        quantity = 5
                    )
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
    //dialogInputs: ItemMainDialogInputs
) {

    FullHeightDialogComponent(onDismissRequest, scaffoldTopBar = {
        ChecklistDialogTopBarComponent(onDismissRequest)
    }, scaffoldContent = { innerPadding ->
        ChecklistDialogContentComponent(
            innerPadding,
            //dialogInputs = dialogInputs
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
                text = "Add Item",
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