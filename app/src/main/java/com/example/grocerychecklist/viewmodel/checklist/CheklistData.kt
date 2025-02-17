package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.ui.component.Measurement
import java.time.LocalDateTime

/**
 * Represents a single item in the checklist.
 *
 * @property id The id of the checklist item.
 * @property itemId The id of the item.
 * @property name The name of the item.
 * @property category The category of the item (e.g., Meat, Vegetable).
 * @property price The price per unit of the item.
 * @property quantity The quantity of the item.
 * @property measurement The unit of measurement for the quantity (e.g., KILOGRAM).
 * @property isChecked Whether the item has been checked off the list.
 */
data class ChecklistData(
    val id: Long,
    val itemId: Long,
    val checklistId: Long,
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Int,
    val measurement: Measurement,
    val measurementValue: Double,
    val photoRef: String,
    val order: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    var isChecked: Boolean = false
)

fun checklistDataMapper(items: List<ChecklistItemFull>): List<ChecklistData> {
    return items.map { item ->
        ChecklistData(
            item.checklistItem.id,
            item.item.id,
            item.checklistItem.checklistId,
            item.item.name,
            ItemCategory.entries.find { it.name == item.item.category } ?: ItemCategory.OTHER,
            item.item.price,
            item.checklistItem.quantity,
            Measurement.entries.find { it.name == item.item.measureType } ?: Measurement.PIECE,
            item.item.measureValue,
            item.item.photoRef,
            item.checklistItem.order,
            item.checklistItem.createdAt,
            item.checklistItem.updatedAt,
        )
    }
}

fun checklistDataMapper(item: ChecklistItemFull): ChecklistData {
    return ChecklistData(
        item.checklistItem.id,
        item.item.id,
        item.checklistItem.checklistId,
        item.item.name,
        ItemCategory.entries.find { it.name == item.item.category } ?: ItemCategory.OTHER,
        item.item.price,
        item.checklistItem.quantity,
        Measurement.entries.find { it.name == item.item.measureType } ?: Measurement.PIECE,
        item.item.measureValue,
        item.item.photoRef,
        item.checklistItem.order,
        item.checklistItem.createdAt,
        item.checklistItem.updatedAt,
    )
}

fun checklistDataMapper(checklistId: Long, items: List<ChecklistData>): List<ChecklistItemFull> {
    return items.map { item ->
        ChecklistItemFull(
            ChecklistItem(
                id = item.id,
                checklistId = checklistId,
                itemId = item.itemId,
                order = item.order,
                quantity = item.quantity,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt
            ),
            Item(
                id = item.itemId,
                name = item.name,
                price = item.price,
                category = item.category.name,
                measureType = item.measurement.name,
                measureValue = item.measurementValue,
                photoRef = item.photoRef,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt
            )
        )
    }
}