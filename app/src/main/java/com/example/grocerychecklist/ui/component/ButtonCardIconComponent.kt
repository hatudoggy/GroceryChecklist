package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ButtonCardIconComponent(
    backgroundColor: Color,
    iconColor: Color = Color.White,
    icon: ImageVector
) {
    val luminance = backgroundColor.luminance()
    val iconColor = if (luminance > 0.65f && iconColor == Color.White) Color.Black else iconColor

    Box(
        modifier = Modifier
            .size(42.5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = "icon",
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonCardIconComponentPreview() {
    ButtonCardIconComponent(
        backgroundColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Filled.Android
    )
}