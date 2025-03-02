package com.example.grocerychecklist.viewmodel.checklist

data class ChecklistViewState (
    val searchQuery: String = "",
    val checklistItems: List<ChecklistData> = emptyList()
)