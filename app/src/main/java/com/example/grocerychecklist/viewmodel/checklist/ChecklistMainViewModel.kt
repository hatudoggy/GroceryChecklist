package com.example.grocerychecklist.viewmodel.checklist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Medication
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.SearchableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ChecklistMainData(
    val name: String,
    val description: String,
    val date: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
)

class ChecklistMainViewModel(
    private val navigator: Navigator
) : SearchableViewModel<ChecklistMainData>(matchesSearch = { item, query -> item.name.contains(query, ignoreCase = true)}) {

    init {
        setItems(
            listOf(
                ChecklistMainData(
                    name = "Main Grocery",
                    description = "A checklist of the main groceries for the month. All the essentials...",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Default.Fastfood,
                    iconBackgroundColor = ChecklistCategory.MAIN_GROCERY.color,
                ),
                ChecklistMainData(
                    name = "Grandpa's Meds",
                    description = "Important to buy it weekly",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Default.Medication,
                    iconBackgroundColor = ChecklistCategory.MEDICINE.color,
                ),
                ChecklistMainData(
                    name = "Holiday Checklist",
                    description = "Checklist for the upcoming holiday",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                    icon = Icons.Default.HolidayVillage,
                    iconBackgroundColor = ChecklistCategory.MAIN_GROCERY.color,
                )
            )
        )
    }

    private val _state = MutableStateFlow(ChecklistMainState())
    val state: StateFlow<ChecklistMainState> = _state.asStateFlow()

//    fun closeDialog() {
//        _dialogState.update { it.copy(isAddingChecklist = false) }
//    }
//
//    fun openDialog() {
//        _dialogState.update { it.copy(isAddingChecklist = true) }
//    }
//
//    fun updateChecklistName(name: String) {
//        _dialogState.update { it.copy(checklistName = name) }
//    }
//
//    fun updateChecklistDescription(description: String) {
//        _dialogState.update { it.copy(checklistDescription = description) }
//    }

    fun onEvent(event: ChecklistMainEvent) {
        when (event) {
            ChecklistMainEvent.NavigateChecklist -> {
                navigator.navigate(Routes.ChecklistDetail)
            }
            ChecklistMainEvent.ToggleDrawer -> {
                _state.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
            }
            ChecklistMainEvent.OpenDrawer -> {
                _state.update { it.copy(isDrawerOpen = true) }
            }
            ChecklistMainEvent.CloseDrawer -> {
                _state.update { it.copy(isDrawerOpen = false) }
            }
            ChecklistMainEvent.ToggleIconPicker -> {
                _state.update { it.copy(isIconPickerOpen = !it.isIconPickerOpen) }
            }
            ChecklistMainEvent.OpenIconPicker -> {
                _state.update { it.copy(isIconPickerOpen = true) }
            }
            ChecklistMainEvent.CloseIconPicker -> {
                _state.update { it.copy(isIconPickerOpen = false) }
            }
        }
    }
}