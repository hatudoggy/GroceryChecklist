package com.example.grocerychecklist.viewmodel.checklist

data class ChecklistViewState (
    val checklistName: String = "",
    val searchQuery: String = "",
    val checklistItems: List<ChecklistData> = emptyList()
)