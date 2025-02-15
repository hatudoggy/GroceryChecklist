package com.example.grocerychecklist.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color

// Bottom sheet modal drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    skipExpand: Boolean? = false,
    onVisibilityChanged: (Boolean) -> Unit = {},
    sheetContent: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipExpand == true
    )

    LaunchedEffect(sheetState.isVisible) {
        onVisibilityChanged(sheetState.isVisible)
    }

    // Closes the sheet when isOpen changes to false
    LaunchedEffect(isOpen) {
        if (!isOpen) {
            sheetState.hide() // Ensures smooth closing before reopening
        }
    }

    // Waits for sheetState.isVisible to turn to false to ensure smooth closing
    // Fixes the snappy opening/closing of the sheet after an action
    if (isOpen || sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            sheetContent()
        }
    }
}