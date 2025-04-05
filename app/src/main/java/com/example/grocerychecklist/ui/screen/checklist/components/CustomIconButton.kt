package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomIconButton(
    icon: ImageVector,
    selectedColor: Color = Color.Blue,
    unselectedColor: Color,
    contentDescription: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var clicked by remember { mutableStateOf(false) }
    var longPressed by remember { mutableStateOf(false) }

    LaunchedEffect(clicked) {
        if (clicked && !longPressed) {
            delay(200)
            clicked = false
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (clicked) selectedColor else unselectedColor,
        modifier = modifier
            .size(24.dp)
            .padding(0.dp)
            .combinedClickable(
                onClick = {
                    longPressed = false
                    clicked = true
                    onClick()
                },
                onLongClick = {
                    longPressed = !longPressed
                    clicked = false
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled
            )
            .minimumInteractiveComponentSize()
    )
}