package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.ChangeSortOption
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.ClearAllFilters
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.ToggleCategorySelection
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartEvent.ToggleSortDirection
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartState


/**
 * [FilterSortBottomSheet] Composable
 *
 * This composable displays a bottom sheet for filtering and sorting checklist items.
 * It allows users to filter items by category and sort them based on various criteria
 * (Name, Category, Price, Quantity, Date Added) in ascending or descending order.
 *
 * @param state The current state of the checklist, including selected categories,
 *        sort option, and sort direction.  This is of type [ChecklistStartState].
 * @param onEvent A callback function to handle user interactions, such as toggling
 *        category selection, clearing filters, changing the sort option, and
 *        toggling the sort direction. This is of type [(ChecklistStartEvent) -> Unit].
 */
@Composable
fun FilterSortBottomSheet(
    state: ChecklistStartState,
    onEvent: (ChecklistStartEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Categories section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filter by Category",
                fontSize = 16.sp,
                color = DarkGray.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = { onEvent(ClearAllFilters) },
                modifier = Modifier
                    .height(32.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text(
                "Clear All Filters",
                fontSize = 12.sp,
                letterSpacing = 0.sp,
                fontWeight = FontWeight.Medium,
            ) }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)


        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ItemCategory.entries) { category ->
                FilterChip(
                    selected = state.selectedCategories.contains(category),
                    onClick = { onEvent(ToggleCategorySelection(category)) },
                    label = { Text(category.name) },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreenSurface.copy(alpha = 0.4f),
                        selectedLabelColor = PrimaryDarkGreen
                    )
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sorting section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "Sort by",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onEvent(ToggleSortDirection) }
            ) {
                Icon(
                    imageVector = if (state.isSortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = "Sort direction",
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = if (state.isSortAscending) "Ascending" else "Descending",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ChecklistItemOrder.entries.forEach { option ->
                if (option == ChecklistItemOrder.Order) return@forEach

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(ChangeSortOption(option)) }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = state.selectedSortOption == option,
                        onClick = null, // null because we're handling click on the whole row
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = when (option) {
                            ChecklistItemOrder.Name -> "Name"
                            ChecklistItemOrder.Category -> "Category"
                            ChecklistItemOrder.Price -> "Price"
                            ChecklistItemOrder.Quantity -> "Quantity"
                            ChecklistItemOrder.Date -> "Date Added"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
