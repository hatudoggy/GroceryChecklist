package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.model.Checklist
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class ChecklistFirestore(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: Long = 0,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("icon") @set:PropertyName("icon")
    var icon: IconOption = IconOption.MAIN_GROCERY,

    @get:PropertyName("iconBackgroundColor") @set:PropertyName("iconBackgroundColor")
    var iconBackgroundColor: ColorOption = ColorOption.White,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp? = null,

    @get:PropertyName("lastOpenedAt") @set:PropertyName("lastOpenedAt")
    var lastOpenedAt: Timestamp? = null,

    @get:PropertyName("lastShopAt") @set:PropertyName("lastShopAt")
    var lastShopAt: Timestamp? = null,
) {
    fun toChecklist(): Checklist {
        return Checklist(
            id = id,
            name = name,
            description = description,
            icon = icon,
            iconBackgroundColor = iconBackgroundColor,
            createdAt = createdAt?.toLocalDateTime(),
            updatedAt = updatedAt?.toLocalDateTime(),
            lastOpenedAt = lastOpenedAt?.toLocalDateTime(),
            lastShopAt = lastShopAt?.toLocalDateTime()
        )
    }

    companion object {
        fun fromChecklist(checklist: Checklist): ChecklistFirestore {
            return ChecklistFirestore(
                id = checklist.id,
                name = checklist.name,
                description = checklist.description,
                icon = checklist.icon,
                iconBackgroundColor = checklist.iconBackgroundColor,
                createdAt = checklist.createdAt?.toTimestamp(),
                updatedAt = checklist.updatedAt?.toTimestamp(),
                lastOpenedAt = checklist.lastOpenedAt?.toTimestamp(),
                lastShopAt = checklist.lastShopAt?.toTimestamp()
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "icon" to icon.name,
            "iconBackgroundColor" to iconBackgroundColor.name,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "lastOpenedAt" to lastOpenedAt,
            "lastShopAt" to lastShopAt
        )
    }
}