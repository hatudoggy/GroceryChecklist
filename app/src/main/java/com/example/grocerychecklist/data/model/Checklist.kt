package com.example.grocerychecklist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Checklist(
    @PrimaryKey(autoGenerate = true)
    val id: Int
)
