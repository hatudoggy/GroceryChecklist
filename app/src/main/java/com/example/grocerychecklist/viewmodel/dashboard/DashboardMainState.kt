package com.example.grocerychecklist.viewmodel.dashboard

import com.example.grocerychecklist.data.model.Checklist

data class DashboardMainState (
    val checklists: List<Checklist> = emptyList(),
    val name: String = ""
)