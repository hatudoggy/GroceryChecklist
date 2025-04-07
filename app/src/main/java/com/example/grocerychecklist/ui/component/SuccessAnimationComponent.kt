package com.example.grocerychecklist.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SuccessAnimationComponent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val circleSize = 100.dp
    val strokeWidth = 8.dp

    // Animation progress (0f to 1f)
    val progress = remember { Animatable(0f) }

    // Start animation when composable is first composed
    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure proper rendering
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Animate checkmark drawing
    val checkmarkProgress = remember { Animatable(0f) }
    LaunchedEffect(progress.value) {
        if (progress.value == 1f) {
            checkmarkProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Canvas(
        modifier = modifier.size(circleSize)
    ) {
        // Draw background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = size.minDimension / 2,
            style = Stroke(width = strokeWidth.toPx())
        )

        // Draw animated progress circle
        val endAngle = 360f * progress.value
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = endAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Draw checkmark when animation is complete
        if (progress.value == 1f) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Checkmark path coordinates (scaled based on circle size)
            val checkmarkPath = listOf(
                Offset(centerX - 40f, centerY),
                Offset(centerX - 25f, centerY + 50f),
                Offset(centerX + 50f, centerY - 50f)
            )

            // Draw checkmark
            drawLine(
                color = color,
                strokeWidth = strokeWidth.toPx(),
                start = checkmarkPath[0],
                end = checkmarkPath[1],
                alpha = checkmarkProgress.value
            )

            drawLine(
                color = color,
                strokeWidth = strokeWidth.toPx(),
                start = checkmarkPath[1],
                end = checkmarkPath[2],
                alpha = checkmarkProgress.value
            )
        }
    }
}

@Preview
@Composable
fun SuccessAnimationComponentPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SuccessAnimationComponent()
    }
}