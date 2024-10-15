package com.example.grocerychecklist.data

import com.example.grocerychecklist.data.mapper.ItemInput

object ItemInputTestMockData {
    val item1 = ItemInput(
        name = "Item 1",
        price = 20.00,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val item2 = ItemInput(
        name = "Item 2",
        price = 85.00,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val item3 = ItemInput(
        name = "Item 3",
        price = 40.00,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val item4 = ItemInput(
        name = "Item 4",
        price = 10.00,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val item5 = ItemInput(
        name = "Item 5",
        price = 60.00,
        category = "Fresh",
        measureType = "kg",
        measureValue = 20.0,
        photoRef = ""
    )

    val updatedItem = ItemInput(
        name = "Walter White Blue Stone",
        price = 69.00,
        category = "Frozen",
        measureType = "g",
        measureValue = 69.0,
        photoRef = "stone.jpg"
    )

    val itemList = listOf(
        item1,
        item2,
        item3,
        item4,
        item5,
    )
}