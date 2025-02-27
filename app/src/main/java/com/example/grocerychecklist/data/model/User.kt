package com.example.grocerychecklist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String = "",
    val isAnonymous: Boolean = true
)
