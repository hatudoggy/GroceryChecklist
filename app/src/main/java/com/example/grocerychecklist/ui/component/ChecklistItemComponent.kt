package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency

enum class ChecklistItemComponentVariant {
    ChecklistItem,
    Item,
}

@Composable
fun ChecklistItemComponent(
    name: String,
    variant: ChecklistItemComponentVariant,
    category: String,
    price: Double,
    showPic: Boolean? = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        if(showPic == true) {
            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    name,
                    fontSize = 16.sp,
                )
                Text(
                    category,
                    color = Color.Gray
                )
            }
            Column {
                val converter = ConvertNumToCurrency()
                Text(
                    converter(Currency.PHP, price, false),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistItemComponentPreview() {
    ChecklistItemComponent(
        name = "Tender Juicy Hotdog",
        variant = ChecklistItemComponentVariant.ChecklistItem,
        category = "Frozen Meat",
        price = 250.00,
    )
}