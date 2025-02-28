package com.example.grocerychecklist.viewmodel.checklist

import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.viewmodel.util.SearchableViewModel
import kotlin.text.contains

class ChecklistViewViewModel(
    private val navigator: Navigator
): SearchableViewModel<ChecklistData>(matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}) {
    init {
//        setItems(
//            listOf(
//                ChecklistData(1, 1, "Chicken Breast", ItemCategory.MEAT, 350.00, 1, Measurement.KILOGRAM),
//                ChecklistData(1, 1, "Ground Beef", ItemCategory.MEAT, 400.00, 2, Measurement.KILOGRAM),
//                ChecklistData(1, 1, "Pork Chop", ItemCategory.MEAT, 300.00, 1, Measurement.KILOGRAM),
//                ChecklistData(1, 1, "Salmon Fillet", ItemCategory.MEAT, 600.00, 5, Measurement.KILOGRAM),
//                ChecklistData(1, 1, "Broccoli", ItemCategory.VEGETABLE, 120.00, 1, Measurement.KILOGRAM),
//                ChecklistData(1, 1, "Carrots", ItemCategory.VEGETABLE, 80.00, 1, Measurement.KILOGRAM),
//            )
//        )
    }

    fun onEvent(event: ChecklistViewEvent) {
        when (event) {
            ChecklistViewEvent.NavigateBack -> { navigator.popBackStack() }
        }
    }
}