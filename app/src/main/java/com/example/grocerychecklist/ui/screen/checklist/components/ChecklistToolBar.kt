package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen
import com.example.grocerychecklist.ui.theme.PrimaryGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistToolbar(
    title: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onToggleDrawer: () -> Unit,
    onToggleFilterBottomSheet: () -> Unit,
    hasSelectedAll: Boolean,
    selectedCount: Int,
    onCancelSelection: () -> Unit,
    onSelectAll: () -> Unit,
    isSelectionModeActive: Boolean,
    modifier: Modifier = Modifier
) {
    val filterButton = remember { mutableStateOf<LayoutCoordinates?>(null) }

    Column(modifier = modifier.border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(percent = 15))) {
        if (isSelectionModeActive) {
            // Selection mode top bar
            TopAppBar(
                title = { Text("$selectedCount selected") },
                navigationIcon = {
                    IconButton(onClick = onCancelSelection) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel selection")
                    }
                },
                actions = {
                    IconButton(onClick = onSelectAll) {
                        Icon(
                            Icons.Default.Checklist,
                            contentDescription = "Select all",
                            tint = if (hasSelectedAll) PrimaryGreen else Color.LightGray
                        )
                    }
                }
            )
        } else {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box(modifier = Modifier.onGloballyPositioned { filterButton.value = it }) {
                        IconButton(onClick = { onToggleFilterBottomSheet() }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Sort options"
                            )
                        }
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },

                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(percent = 50)
                    ),
                fontSize = 16.sp,
                placeholderText = "Search",
                value = searchQuery,
                onValueChange = onSearchChange
            )

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onToggleDrawer,
            ) {
                Icon(
                    Icons.Outlined.AddCircleOutline,
                    contentDescription = "Add item",
                    tint = PrimaryDarkGreen
                )
            }
        }
    }
}
