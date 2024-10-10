package com.example.grocerychecklist.viewmodel._sampleViewModel

data class ContactState (
    val contacts: List<String> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val sortType: String = "name"
)