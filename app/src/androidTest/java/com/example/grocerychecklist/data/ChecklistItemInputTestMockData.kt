package com.example.grocerychecklist.data

import com.example.grocerychecklist.data.mapper.ChecklistItemInput

object ChecklistItemInputTestMockData {

    val checklistItem1 = ChecklistItemInput(
        name = "Checklist Item 1",
        price = 20.00,
        quantity = 4,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val checklistItem2 = ChecklistItemInput(
        name = "Checklist Item 2",
        price = 85.00,
        quantity = 5,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val checklistItem3 = ChecklistItemInput(
        name = "Checklist Item 3",
        price = 40.00,
        quantity = 6,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val checklistItem4 = ChecklistItemInput(
        name = "Checklist Item 4",
        price = 10.00,
        quantity = 7,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val checklistItem5 = ChecklistItemInput(
        name = "Checklist Item 5",
        price = 60.00,
        quantity = 8,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val updatedChecklistItem = ChecklistItemInput(
        name = "Walter White Blue Stone",
        price = 69.00,
        quantity = 7,
        category = "Frozen",
        measureType = "g",
        measureValue = 69.0,
        photoRef = "stone.jpg"
    )

    val checklistItemList = listOf(
        checklistItem1,
        checklistItem2,
        checklistItem3,
        checklistItem4,
        checklistItem5,
    )
}