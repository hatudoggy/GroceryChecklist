package com.example.grocerychecklist.data.model

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
    val id: Int = 0,
    val checklistId: Int,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
)
