package com.example.grocerychecklist.viewmodel.item

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.model.Item
import com.example.grocerychecklist.data.repository.ItemOrder
import com.example.grocerychecklist.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemMainViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ItemMainState())
    val state: StateFlow<ItemMainState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        ItemMainState()
    )

    init {
        loadItems()
    }

    fun onEvent(event: ItemMainEvent) {
        when (event) {
            is ItemMainEvent.OpenDialog -> {
                _state.update { it.copy(isAddingItem = true, selectedItem = null) }
            }

            is ItemMainEvent.CloseDialog -> {
                _state.update { it.copy(isAddingItem = false, selectedItem = null) }
            }

            is ItemMainEvent.SelectItem -> {
                _state.update { it.copy(isAddingItem = true, selectedItem = event.item) }
            }

            is ItemMainEvent.AddItem -> {
                viewModelScope.launch {
                    try {
                        itemRepository.addItem(event.itemInput)
                        loadItems()
                    } catch (e: Exception) {
                        Log.e("ItemMainViewModel", "Error adding item: ${e.message}")
                    }
                }
                _state.update { it.copy(isAddingItem = false) }
            }

            is ItemMainEvent.EditItem -> {
                viewModelScope.launch {
                    try {
                        itemRepository.updateItem(event.id, event.itemInput)
                        loadItems()
                    } catch (e: Exception) {
                        Log.e("ItemMainViewModel", "Error editing item: ${e.message}")
                    }
                }
                _state.update { it.copy(isAddingItem = false, selectedItem = null) }
            }

            is ItemMainEvent.DeleteItem -> {
                viewModelScope.launch {
                    try {
                        itemRepository.deleteItem(event.item)
                        loadItems()
                    } catch (e: Exception) {
                        Log.e("ItemMainViewModel", "Error deleting item: ${e.message}")
                    }
                }
                _state.update { it.copy(isAddingItem = false, selectedItem = null) }
            }
        }
    }

    fun getItemCategoryFromString(categoryText: String): ItemCategory? {
        return ItemCategory.entries.firstOrNull { it.text.equals(categoryText, ignoreCase = true) }
    }

    private fun loadItems() {
        viewModelScope.launch {
            itemRepository.getItems(ItemOrder.CreatedAt)
                .catch { emit(emptyList<Item>()) }
                .collect { itemList -> _state.update { it.copy(items = itemList) } }
        }
    }
}