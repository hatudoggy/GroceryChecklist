package com.example.grocerychecklist.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ChecklistInput(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
)
