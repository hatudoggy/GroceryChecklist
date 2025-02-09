package com.example.grocerychecklist.viewmodel.item

import com.example.grocerychecklist.data.mapper.ItemInput

sealed interface ItemMainEvent {
    data object OpenDialog: ItemMainEvent
    data object CloseDialog: ItemMainEvent
    data class AddItem(val itemInput: ItemInput) : ItemMainEvent
}