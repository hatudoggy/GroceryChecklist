package com.example.grocerychecklist.data.mapper

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption

data class ChecklistInput(
    val name: String,
    val description: String,
    val icon: IconOption = IconOption.MAIN_GROCERY,
    val iconBackgroundColor: ColorOption = ColorOption.CopySkyGreen,
)
