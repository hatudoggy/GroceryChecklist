package com.example.grocerychecklist.viewmodel.item

sealed interface ItemMainEvent {
    data object OpenDialog: ItemMainEvent
    data object CloseDialog: ItemMainEvent
}