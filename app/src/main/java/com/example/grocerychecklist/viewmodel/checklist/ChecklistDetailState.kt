package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.data.model.Checklist

sealed class ChecklistDetailState {
        data object Loading : ChecklistDetailState()
        data class Loaded(val checklist: Checklist) : ChecklistDetailState()
        data object Error : ChecklistDetailState()
}
