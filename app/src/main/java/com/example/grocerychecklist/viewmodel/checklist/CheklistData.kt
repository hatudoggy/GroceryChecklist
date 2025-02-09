package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.ui.component.Measurement

/**
 * Represents a single item in the checklist.
 *
 * @property name The name of the item.
 * @property category The category of the item (e.g., Meat, Vegetable).
 * @property price The price per unit of the item.
 * @property quantity The quantity of the item.
 * @property measurement The unit of measurement for the quantity (e.g., KILOGRAM).
 * @property isChecked Whether the item has been checked off the list.
 */
data class ChecklistData(
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Double,
    val measurement: Measurement,
    var isChecked: Boolean = false
)
