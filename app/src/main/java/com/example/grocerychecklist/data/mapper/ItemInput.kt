package com.example.grocerychecklist.data.mapper

data class ItemInput(
    val name: String,
    val price: Double,
    val category: String,
    val measureType: String,
    val measureValue: Double,
    val photoRef: String,
)
