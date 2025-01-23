package com.example.grocerychecklist.ui.component

import ItemCategory
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HistoryChipGroup(
    categories: List<ItemCategory>,
    onCategorySelected: (ItemCategory) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ItemCategory.ALL) }

    categories.forEach { category ->
        HistoryChipComponent(
            category = category,
            isSelected = category == selectedCategory,
            onSelected = {
                selectedCategory = category
                onCategorySelected(category)
            }
        )
    }
}

//@Preview
//@Composable
//fun HistoryChipGroupPreview() {
//    HistoryChipGroup(
//        categories = ItemCategory.values().toList(),
//        onCategorySelected = {}
//    )
//}

@Composable
fun HistoryChipComponent(
    category: ItemCategory,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        onClick = onSelected,
        label = { Text(category.text) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = if (isSelected) category.color else Color(0xFFE5E5EA),
            labelColor = if (isSelected) Color.White else Color(0xFFA5A5A5),
            selectedContainerColor = category.color,
            selectedLabelColor = Color.White,
            disabledContainerColor = Color(0xFFF0F0F0),
            disabledLabelColor = Color(0xFFA5A5A5)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) category.color else Color(0xFFE5E5EA)
        ),
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Preview
@Composable
private fun HistoryChipPreview(){
    HistoryChipComponent(ItemCategory.ALL, isSelected = true, onSelected = {})
}
