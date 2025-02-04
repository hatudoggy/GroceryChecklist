package com.example.grocerychecklist.ui.component

import ItemCategory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySelector(
    selectedCategories: Set<ItemCategory>,
    onCategorySelected: (ItemCategory) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(start = 12.dp, top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(ItemCategory.entries) { category ->
            CategoryChipComponent(
                category = category,
                isSelected = selectedCategories.contains(category),
                onSelected = { onCategorySelected(category) }
            )
        }
    }
}
