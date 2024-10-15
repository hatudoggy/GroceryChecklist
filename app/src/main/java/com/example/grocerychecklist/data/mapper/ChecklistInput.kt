package com.example.grocerychecklist.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption

data class ChecklistInput(
    val name: String,
    val description: String,
    val icon: IconOption,
    val iconColor: ColorOption,
)
