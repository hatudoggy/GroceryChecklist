package com.example.grocerychecklist.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ChecklistItemFull(
    @Embedded
    val checklistItem: ChecklistItem,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item:Item
)
