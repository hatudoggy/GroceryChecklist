package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.repository.ChecklistDetails

sealed interface ChecklistDetailUIState {
        data class Success(
                val checklistDetails: ChecklistDetails
        ) : ChecklistDetailUIState
        object Loading : ChecklistDetailUIState
        data class Error(val message: String? = null) : ChecklistDetailUIState
}
