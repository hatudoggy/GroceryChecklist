package com.example.grocerychecklist.viewmodel._sampleViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ContactViewModel: ViewModel() {

    private val sortType = MutableStateFlow("name")
    private val state = MutableStateFlow(ContactState())

    fun onEvent(event: ContactEvent) {
        when(event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    //dao.deleteContact(event.contact)
                }
            }
            ContactEvent.HideDialog -> {
                state.update { it.copy(
                    isAddingContact = false
                ) }
            }
            ContactEvent.SaveContact -> TODO()
            is ContactEvent.SetFirstName -> TODO()
            is ContactEvent.SetLastName -> TODO()
            is ContactEvent.SetPhoneNumber -> TODO()
            ContactEvent.ShowDialog -> TODO()
            is ContactEvent.SortContacts -> TODO()
        }
    }
}