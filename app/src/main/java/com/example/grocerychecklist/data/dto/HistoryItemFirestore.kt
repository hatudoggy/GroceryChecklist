package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.model.HistoryItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

data class HistoryItemFirestore(
    var id: Long = 0,

    @get:PropertyName("historyId") @set:PropertyName("historyId")
    var historyId: Long = 0,

    @get:PropertyName("checklistItemId") @set:PropertyName("checklistItemId")
    var checklistItemId: Long = 0,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("price") @set:PropertyName("price")
    var price: Double = 0.0,

    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = "",

    @get:PropertyName("measureType") @set:PropertyName("measureType")
    var measureType: String = "",

    @get:PropertyName("measureValue") @set:PropertyName("measureValue")
    var measureValue: Double = 0.0,

    @get:PropertyName("photoRef") @set:PropertyName("photoRef")
    var photoRef: String = "",

    @get:PropertyName("order") @set:PropertyName("order")
    var order: Int = 0,

    @get:PropertyName("quantity") @set:PropertyName("quantity")
    var quantity: Int = 0,

    @get:PropertyName("isChecked") @set:PropertyName("isChecked")
    var isChecked: Boolean = false,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null
) {
    fun toHistoryItem(id: Long): HistoryItem {
        return HistoryItem(
            id = id,
            historyId = historyId,
            checklistItemId = checklistItemId,
            name = name,
            price = price,
            category = category,
            measureType = measureType,
            measureValue = measureValue,
            photoRef = photoRef,
            order = order,
            quantity = quantity,
            isChecked = isChecked,
            createdAt = createdAt?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }

    companion object {
        fun fromHistoryItem(historyItem: HistoryItem): HistoryItemFirestore {
            return HistoryItemFirestore(
                historyId = historyItem.historyId,
                checklistItemId = historyItem.checklistItemId,
                name = historyItem.name,
                price = historyItem.price,
                category = historyItem.category,
                measureType = historyItem.measureType,
                measureValue = historyItem.measureValue,
                photoRef = historyItem.photoRef,
                order = historyItem.order,
                quantity = historyItem.quantity,
                isChecked = historyItem.isChecked,
                createdAt = historyItem.createdAt.toTimestamp()
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "historyId" to historyId,
            "checklistItemId" to checklistItemId,
            "name" to name,
            "price" to price,
            "category" to category,
            "measureType" to measureType,
            "measureValue" to measureValue,
            "photoRef" to photoRef,
            "order" to order,
            "quantity" to quantity,
            "isChecked" to isChecked,
            "createdAt" to createdAt
        )
    }
}