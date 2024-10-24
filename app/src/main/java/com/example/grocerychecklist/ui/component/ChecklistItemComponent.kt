package com.example.grocerychecklist.ui.component

import ItemCategory
import ItemTagComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency

enum class ChecklistItemComponentVariant {
    ChecklistRadioItem,
    ChecklistItem,
    Item,
}

@Composable
fun ChecklistItemComponent(
    name: String,
    variant: ChecklistItemComponentVariant,
    category: ItemCategory,
    price: Double,
    quantity: Int,
    picRef: String? = "",
    showPic: Boolean? = false,
    isChecked: Boolean? = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        if (
            variant == ChecklistItemComponentVariant.ChecklistRadioItem
        ) {
            ChecklistRadioButton(isChecked == true)
            Spacer(
                modifier = Modifier.width(14.dp)
            )
        }
        if(showPic == true) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(38.dp)
                    .background(Color.Blue)
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        textDecoration = if (isChecked == true)
                            TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isChecked == true)
                        MaterialTheme.colorScheme.primary else Color.Black,
                )
                Row {
                    Spacer(Modifier.width(2.dp))
                    ItemTagComponent(category)
                }
            }
            Column (
                horizontalAlignment = AbsoluteAlignment.Right
            ) {
                val converter = ConvertNumToCurrency()
                Text(
                    converter(Currency.PHP, price, false),
                    fontSize = 16.sp
                )
                if (
                    variant == ChecklistItemComponentVariant.ChecklistItem ||
                    variant == ChecklistItemComponentVariant.ChecklistRadioItem
                ) {
                    Text(
                        "$quantity x",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistRadioItemComponentPreview() {
    ChecklistItemComponent(
        name = "Tender Juicy Hotdog",
        variant = ChecklistItemComponentVariant.ChecklistRadioItem,
        category = ItemCategory.MEAT,
        price = 250.00,
        showPic = true,
        isChecked = false,
        quantity = 4
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistRadioItemCheckedComponentPreview() {
    ChecklistItemComponent(
        name = "Tender Juicy Hotdog",
        variant = ChecklistItemComponentVariant.ChecklistRadioItem,
        category = ItemCategory.MEAT,
        price = 250.00,
        showPic = true,
        isChecked = true,
        quantity = 4
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistItemComponentPreview() {
    ChecklistItemComponent(
        name = "Tender Juicy Hotdog",
        variant = ChecklistItemComponentVariant.ChecklistItem,
        category = ItemCategory.MEAT,
        price = 250.00,
        showPic = true,
        quantity = 4
    )
}

@Preview(showBackground = true)
@Composable
fun ItemComponentPreview() {
    ChecklistItemComponent(
        name = "Alpine Drinking Water",
        variant = ChecklistItemComponentVariant.Item,
        category = ItemCategory.CLEANING,
        price = 250.00,
        showPic = false,
        quantity = 4
    )
}