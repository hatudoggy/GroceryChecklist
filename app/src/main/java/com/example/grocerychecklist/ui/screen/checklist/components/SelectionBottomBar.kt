package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditOff
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.theme.LightGray
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.ui.theme.SteelGray

/**
 * Composable function that displays a bottom bar with actions for selected items.
 *
 * This bottom bar provides actions such as delete, copy, edit, and more options,
 * which are conditionally enabled based on the number of selected items.
 *
 * @param selectedCount The number of items currently selected.
 * @param onDelete Callback function to be executed when the "Delete" action is triggered.
 * @param onEditItem Callback function to be executed when the "Edit" action is triggered.
 *   The "Edit" action is only enabled when a single item is selected.
 * @param onMoreActions Callback function to be executed when the "More" action is triggered.
 * @param onCopyToNewChecklist Callback function to be executed when the "Copy to New Checklist"
 *   action is triggered.
 * @param modifier Optional [Modifier] for styling and layout customization.
 */
@Composable
fun SelectionBottomBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onEditItem: () -> Unit,
    onMoreActions: () -> Unit,
    onCopyToNewChecklist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMultipleSelected = selectedCount > 1
    val hasSelectedItems = selectedCount > 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .border(0.1.dp, MaterialTheme.colorScheme.outline)
            .navigationBarsPadding(),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ){
                CustomIconButton(
                    icon = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    onClick = onDelete,
                    selectedColor = PrimaryGreenSurface,
                    unselectedColor = if (hasSelectedItems) DarkGray else LightGray,
                    enabled = hasSelectedItems
                )

                Text(
                    "Delete",
                    fontSize = 12.sp,
                    color = SteelGray,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ){
                CustomIconButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = "Copy to New Checklist",
                    onClick = onCopyToNewChecklist,
                    selectedColor = PrimaryGreenSurface,
                    unselectedColor = if (hasSelectedItems) DarkGray else LightGray,
                    enabled = hasSelectedItems
                )

                Text(
                    "Copy",
                    fontSize = 12.sp,
                    color = SteelGray,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ){
                CustomIconButton(
                    icon = if (!isMultipleSelected) Icons.Outlined.Edit else Icons.Outlined.EditOff,
                    contentDescription = "Edit Item",
                    onClick = onEditItem,
                    selectedColor = PrimaryGreenSurface,
                    unselectedColor = if (!isMultipleSelected) DarkGray else LightGray,
                    enabled = !isMultipleSelected
                )

                Text(
                    "Edit",
                    fontSize = 12.sp,
                    color = SteelGray,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ){
                CustomIconButton(
                    icon = Icons.Outlined.MoreHoriz,
                    contentDescription = "More",
                    onClick = onMoreActions,
                    selectedColor = PrimaryGreenSurface,
                    unselectedColor = DarkGray,
                )

                Text(
                    "More",
                    fontSize = 12.sp,
                    color = SteelGray,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
        }
    }
}