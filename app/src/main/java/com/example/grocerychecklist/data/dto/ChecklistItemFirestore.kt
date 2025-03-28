package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.model.ChecklistItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

data class ChecklistItemFirestore(
    var id: Long = 0,

    @get:PropertyName("checklistId") @set:PropertyName("checklistId")
    var checklistId: Long = 0,

    @get:PropertyName("itemId") @set:PropertyName("itemId")
    var itemId: Long = 0,

    @get:PropertyName("order") @set:PropertyName("order")
    var order: Int = 0,

    @get:PropertyName("quantity") @set:PropertyName("quantity")
    var quantity: Int = 0,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp? = null,
) {
    fun toChecklistItem(id: Long): ChecklistItem {
        return ChecklistItem(
            id = id,
            checklistId = checklistId,
            itemId = itemId,
            order = order,
            quantity = quantity,
            createdAt = createdAt?.toLocalDateTime() ?: LocalDateTime.now(),
            updatedAt = updatedAt?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }

    companion object {
        fun fromChecklistItem(checklistItem: ChecklistItem):ChecklistItemFirestore {
            return ChecklistItemFirestore(
                checklistId = checklistItem.checklistId,
                itemId = checklistItem.itemId,
                order = checklistItem.order,
                quantity = checklistItem.quantity,
                createdAt = checklistItem.createdAt.toTimestamp(),
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "checklistId" to checklistId,
            "itemId" to itemId,
            "order" to order,
            "quantity" to quantity,
        )
    }
}