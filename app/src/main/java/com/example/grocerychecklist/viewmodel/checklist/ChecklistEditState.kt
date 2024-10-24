package com.example.grocerychecklist.viewmodel.checklist

data class ChecklistEditState (
    val isAddingChecklistItem: Boolean = false,
    val itemName: String = "",
    val itemCategory: String = "",
    val itemPrice: String = "",
    val itemQuantity: String = "",
    //val checklistDescription: String = ""
)

