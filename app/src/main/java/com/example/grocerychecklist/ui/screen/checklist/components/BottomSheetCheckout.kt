package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.BottomSheet
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemData

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
                    "Checkout Summary",
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

