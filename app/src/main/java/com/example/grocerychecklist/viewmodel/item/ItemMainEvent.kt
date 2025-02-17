package com.example.grocerychecklist.viewmodel.item

import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.data.model.Item

sealed interface ItemMainEvent {
    data object OpenDialog : ItemMainEvent
    data object CloseDialog : ItemMainEvent
    data class SelectItem(val item: Item) : ItemMainEvent
    data class AddItem(val itemInput: ItemInput) : ItemMainEvent
    data class EditItem(val id: Long, val itemInput: ItemInput) : ItemMainEvent
    data class DeleteItem(val item: Item) : ItemMainEvent
}
