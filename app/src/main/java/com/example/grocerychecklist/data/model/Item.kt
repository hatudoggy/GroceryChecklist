package com.example.grocerychecklist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    val measureType: String,
    val measureValue: Double,
    val photoRef: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
