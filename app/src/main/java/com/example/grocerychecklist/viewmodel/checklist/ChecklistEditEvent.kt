package com.example.grocerychecklist.viewmodel.checklist

sealed interface ChecklistEditEvent {
    data object NavigateBack: ChecklistEditEvent
    data object OpenDialog: ChecklistEditEvent
    data object CloseDialog: ChecklistEditEvent
    data class SetItemName(val name: String): ChecklistEditEvent
    data class SetItemCategory(val category: String): ChecklistEditEvent
    data class SetItemPrice(val price: String): ChecklistEditEvent
    data class SetItemQuantity(val quantity: String): ChecklistEditEvent
}