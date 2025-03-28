package com.example.grocerychecklist.data.model

data class AuthUser(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String = "",
    val isAnonymous: Boolean = true
)
