package com.example.grocerychecklist.data.mapper

data class ChecklistItemInput(
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val measureType: String,
    val measureValue: Double,
    val photoRef: String,
)
