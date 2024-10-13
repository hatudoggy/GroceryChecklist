package com.example.grocerychecklist.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Checklist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val lastOpenedAt: LocalDateTime?,
    val lastShopAt: LocalDateTime?,
)
