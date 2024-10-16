package com.example.grocerychecklist.ui.component

import androidx.compose.ui.graphics.Color
import com.example.grocerychecklist.ui.theme.Maroon
import com.example.grocerychecklist.ui.theme.SkyGreen

enum class ChecklistCategory(val color: Color, val text: String) {
    MAIN_GROCERY(SkyGreen, "Main Grocery"),
    MEDICINE(Maroon, "Medicine"),
}