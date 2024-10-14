package com.example.grocerychecklist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = History::class,
            parentColumns = ["id"],
            childColumns = ["historyId"],
        ),
        ForeignKey(
            entity = ChecklistItem::class,
            parentColumns = ["id"],
            childColumns = ["checklistItemId"],
        )
    ]
)
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val historyId: Long,
    @ColumnInfo(index = true)
    val checklistItemId: Long,
    val name: String,
    val price: Double,
    val category: String,
    val measureType: String,
    val measureValue: Double,
    val photoRef: String,
    val order: Int,
    val quantity: Int,
    val isChecked: Boolean,
    val createdAt: LocalDateTime
)
