package com.example.grocerychecklist.ui.screen.checklist

import ItemCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.ChipComponent
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent

@Composable
fun ChecklistStartScreen(
    //state: DashboardBreakdownState,
    onEvent: (ChecklistStartEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
//            FloatingActionButton(
//                shape = CircleShape,
//                onClick = {
//                    /*TODO*/
//                }
//            ) {
//                Icon(Icons.Filled.Add, "Add FAB")
//            }
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
                ChipComponent(
                    label = "All",
                    isActive = true
                )
                ChipComponent(
                    label = "Checked"
                )
                ChipComponent(
                    label = "Unchecked"
                )
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(5) {
                    ChecklistItemComponent(
                        name = "Tender Juicy Hot dog",
                        variant = ChecklistItemComponentVariant.ChecklistRadioItem,
                        category = ItemCategory.MEAT,
                        price = 250.00,
                        quantity = 5
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val converter = ConvertNumToCurrency()
                Text(
                    "Total",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    converter(Currency.PHP, 250.00, false),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedTextField(
                    leadingIcon = {

                    },
                    modifier = Modifier
                        .fillMaxWidth(0f)
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(percent = 50)
                        ),
                    fontSize = 16.sp,
                    placeholderText = "Add Item"
                )
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = "Checkout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun ChecklistStartScreenPreview() {
    ChecklistStartScreen(
        onEvent = {}
    )
}