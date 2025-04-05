package com.example.grocerychecklist.ui.screen.checklist.components

import android.R.attr.paddingBottom
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.ui.theme.PrimaryLightGreen
import com.example.grocerychecklist.ui.theme.SecondaryColorSurface
import com.example.grocerychecklist.ui.theme.WheatBrown
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChecklistFAB(
    currentMode: ChecklistMode,
    hasItems: Boolean,
    hasSelectedItems: Boolean,
    onAddItem: () -> Unit,
    onToggleMode: () -> Unit,
    onDeleteSelected: () -> Unit,
    paddingBottom: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = paddingBottom)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            },
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Show extended FAB menu when expanded and not dragging
        if (expanded && !isDragging) {
            Column(
                modifier = Modifier.padding(bottom = 32.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentMode == ChecklistMode.SHOPPING) {
                    // Shopping mode actions
                    ExtendedFloatingActionButton(
                        onClick = {
                            onAddItem()
                            expanded = false
                        },
                        icon = { Icon(Icons.Default.Add, "Add item") },
                        text = { Text("Add Item") },
                        containerColor = SecondaryColorSurface
                    )

                    ExtendedFloatingActionButton(
                        onClick = {
                            onToggleMode()
                            expanded = false
                        },
                        icon = { Icon(Icons.Default.Edit, "Edit mode") },
                        text = { Text("Start Editing") },
                        containerColor = PrimaryGreen
                    )
                } else {
                    // Edit mode actions
                    if (hasItems) {
                        if (hasSelectedItems) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    onDeleteSelected()
                                    expanded = false
                                },
                                icon = { Icon(Icons.Default.Delete, "Delete selected") },
                                text = { Text("Delete Selected") },
                                containerColor = ErrorTonal
                            )
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = {
                            onAddItem()
                            expanded = false
                        },
                        icon = { Icon(Icons.Default.Add, "Add item") },
                        text = { Text("Add Item") },
                        containerColor = SecondaryColorSurface
                    )

                    ExtendedFloatingActionButton(
                        onClick = {
                            onToggleMode()
                            expanded = false
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, "Shopping mode") },
                        text = { Text("Start Shopping") },
                        containerColor = PrimaryGreen
                    )
                }
            }
        }

        // Main FAB button
        FloatingActionButton(
            onClick = { if (!isDragging) expanded = !expanded },
            containerColor = PrimaryGreen,
        ) {
            Icon(
                if (expanded) Icons.Default.ChevronRight else Icons.Default.Add,
                contentDescription = if (expanded) "Close menu" else "Open menu"
            )
        }
    }
}

@Preview
@Composable
fun ChecklistFABPreviewEdit() {
    ChecklistFAB(
        currentMode = ChecklistMode.EDIT,
        hasItems = true,
        hasSelectedItems = true,
        onAddItem = {},
        onToggleMode = {},
        onDeleteSelected = {},
        paddingBottom = 32.dp
    )
}

@Preview
@Composable
fun ChecklistFABPreviewShopping() {
    ChecklistFAB(
        currentMode = ChecklistMode.SHOPPING,
        hasItems = true,
        hasSelectedItems = true,
        onAddItem = {},
        onToggleMode = {},
        onDeleteSelected = {}
    )
}

