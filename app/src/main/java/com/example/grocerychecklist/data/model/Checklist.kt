package com.example.grocerychecklist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import java.time.LocalDateTime

@Entity
data class Checklist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val icon: IconOption,
    val iconBackgroundColor: ColorOption,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val lastOpenedAt: LocalDateTime?,
    val lastShopAt: LocalDateTime?,
)
