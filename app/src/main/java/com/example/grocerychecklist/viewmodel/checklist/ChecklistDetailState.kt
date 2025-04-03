package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.repository.ChecklistDetails

sealed interface ChecklistDetailState {
        data class Success(
                val checklistDetails: ChecklistDetails,
        ) : ChecklistDetailState
        object Loading : ChecklistDetailState
        data class Error(val message: String? = null) : ChecklistDetailState
}
