package com.example.grocerychecklist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Checklist::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
        )
    ]
)
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val checklistId: Long,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
)
