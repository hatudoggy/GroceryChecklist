package com.example.grocerychecklist.data

import com.example.grocerychecklist.data.mapper.ChecklistInput

object ChecklistInputTestMockData {

    val checklist1 = ChecklistInput(
        name = "Checklist 1",
        description = "A test checklist 1",
        icon = IconOption.Android,
        iconColor = ColorOption.White
    )

    val checklist2 = ChecklistInput(
        name = "Checklist 2",
        description = "A test checklist 2",
        icon = IconOption.Android,
        iconColor = ColorOption.White
    )

    val checklist3 = ChecklistInput(
        name = "Checklist 3",
        description = "A test checklist 3",
        icon = IconOption.Android,
        iconColor = ColorOption.White
    )

    val updatedChecklist = ChecklistInput(
        name = "Checklist Updated",
        description = "Hep hep Horay!",
        icon = IconOption.Home,
        iconColor = ColorOption.Black
    )

    val checklistList = listOf(
        checklist1,
        checklist2,
        checklist3
    )

}