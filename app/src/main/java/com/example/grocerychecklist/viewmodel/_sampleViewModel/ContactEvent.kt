package com.example.grocerychecklist.viewmodel._sampleViewModel

sealed interface ContactEvent {
    data object SaveContact: ContactEvent
    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent
    data object ShowDialog: ContactEvent
    data object HideDialog: ContactEvent
    data class SortContacts(val sortType: String): ContactEvent
    data class DeleteContact(val contactId: Int): ContactEvent
}