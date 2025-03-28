package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.model.Item
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

class ItemFirestore (
    var id: Long = 0,

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

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp? = null,
) {
    fun toItem(id: Long): Item {
        return Item(
            id = id,
            name = name,
            price = price,
            category = category,
            measureType = measureType,
            measureValue = measureValue,
            photoRef = photoRef,
            createdAt = createdAt?.toLocalDateTime(),
            updatedAt = updatedAt?.toLocalDateTime()
        )
    }

    companion object {
        fun fromItem(item: Item): ItemFirestore {
            return ItemFirestore(
                name = item.name,
                price = item.price,
                category = item.category,
                measureType = item.measureType,
                measureValue = item.measureValue,
                photoRef = item.photoRef,
                createdAt = item.createdAt?.toTimestamp(),
                updatedAt = item.updatedAt?.toTimestamp()
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "measureType" to measureType,
            "measureValue" to measureValue,
            "photoRef" to photoRef,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}