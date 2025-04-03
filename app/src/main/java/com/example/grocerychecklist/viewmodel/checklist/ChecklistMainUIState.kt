package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.model.Checklist

sealed interface ChecklistMainUIState {
    data object Loading : ChecklistMainUIState
    data object Empty : ChecklistMainUIState
    data class Error(val message: String?) : ChecklistMainUIState
    data class Success(val data: List<Checklist>) : ChecklistMainUIState
}