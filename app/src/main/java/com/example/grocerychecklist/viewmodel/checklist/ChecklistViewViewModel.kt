package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.viewmodel.SearchableViewModel
import kotlin.text.contains

class ChecklistViewViewModel(
    private val navigator: Navigator
): SearchableViewModel<ChecklistData>(matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}) {
    init {
        setItems(
            listOf(
                ChecklistData("Chicken Breast", ItemCategory.MEAT, 350.00, 1.5, Measurement.KILOGRAM),
                ChecklistData("Ground Beef", ItemCategory.MEAT, 400.00, 2.0, Measurement.KILOGRAM),
                ChecklistData("Pork Chop", ItemCategory.MEAT, 300.00, 1.0, Measurement.KILOGRAM),
                ChecklistData("Salmon Fillet", ItemCategory.MEAT, 600.00, 0.5, Measurement.KILOGRAM),
                ChecklistData("Broccoli", ItemCategory.VEGETABLE, 120.00, 1.0, Measurement.KILOGRAM),
                ChecklistData("Carrots", ItemCategory.VEGETABLE, 80.00, 1.0, Measurement.KILOGRAM),
            )
        )
    }

    fun onEvent(event: ChecklistViewEvent) {
        when (event) {
            ChecklistViewEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}