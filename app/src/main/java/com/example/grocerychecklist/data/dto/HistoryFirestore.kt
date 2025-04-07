package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.model.History
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

data class HistoryFirestore(
    var id: Long = 0,

    @get:PropertyName("checklistId") @set:PropertyName("checklistId")
    var checklistId: Long = 0,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("icon") @set:PropertyName("icon")
    var icon: IconOption = IconOption.MAIN_GROCERY,

    @get:PropertyName("iconColor") @set:PropertyName("iconColor")
    var iconColor: ColorOption = ColorOption.White,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,
) {
    fun toHistory(id: Long): History {
        return History(
            id = id,
            checklistId = checklistId,
            name = name,
            description = description,
            icon = icon,
            iconColor = iconColor,
            createdAt = createdAt?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }

    companion object {
        fun fromHistory(
            history: History
        ): HistoryFirestore {
            return HistoryFirestore(
                checklistId = history.checklistId,
                name = history.name,
                description = history.description,
                icon = history.icon,
                iconColor = history.iconColor,
                createdAt = history.createdAt.toTimestamp(),
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "checklistId" to checklistId,
            "name" to name,
            "description" to description,
            "icon" to icon.name,
            "iconColor" to iconColor.name,
            "createdAt" to createdAt,
        )
    }
}