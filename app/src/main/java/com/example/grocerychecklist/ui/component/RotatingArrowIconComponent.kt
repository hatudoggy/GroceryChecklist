package com.example.grocerychecklist.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RotatingArrowIconComponent(iconColor: Color, isClicked: Boolean) {
    val anim = remember { Animatable(0f) }

    LaunchedEffect(isClicked) {
        val targetValue =
            if (isClicked) 180f else 0f
        anim.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Icon(
        Icons.Default.KeyboardArrowDown, contentDescription = "icon",
        tint = iconColor,
        modifier = Modifier.rotate(anim.value)
    )
}

@Preview(showBackground = true)
@Composable
fun RotatingArrowIconComponentPreview() {
    RotatingArrowIconComponent(iconColor = MaterialTheme.colorScheme.primary, isClicked = true)
}