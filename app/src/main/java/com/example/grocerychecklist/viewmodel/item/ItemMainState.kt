package com.example.grocerychecklist.viewmodel.item

import com.example.grocerychecklist.data.model.Item

data class ItemMainState (
    val isAddingItem: Boolean = false,
    val items: List<Item> = emptyList(),
    val selectedItem: Item? = null
)