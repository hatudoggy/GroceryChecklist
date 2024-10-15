package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HistoryChipGroup(
    categories: List<ItemCategory>,
    onCategorySelected: (ItemCategory) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

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

@Composable
fun HistoryChipComponent(
    category: ItemCategory,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        onClick = onSelected,
        label = {
            Text(category.text)
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFE7FFC0),
            labelColor = Color(0xFF6FA539),
            selectedContainerColor = category.color,
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = category.color
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
