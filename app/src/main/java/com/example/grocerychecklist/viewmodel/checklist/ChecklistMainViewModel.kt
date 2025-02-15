package com.example.grocerychecklist.viewmodel.checklist

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Medication
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.SearchableViewModel
import com.example.grocerychecklist.viewmodel.item.ItemMainEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val navigator: Navigator, private val checklistRepository: ChecklistRepository
) : SearchableViewModel<ChecklistMainData>(matchesSearch = { item, query ->
    item.name.contains(
        query, ignoreCase = true
    )
}) {

    // Create a search job to debounce user input
    private var searchJob: Job? = null
    private val _state = MutableStateFlow(ChecklistMainState())
    val state: StateFlow<ChecklistMainState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), ChecklistMainState()
    )

    init {
        loadChecklists()
    }

    fun onEvent(event: ChecklistMainEvent) {
        when (event) {
            is ChecklistMainEvent.NavigateChecklist -> {
                navigator.navigate(Routes.ChecklistDetail(event.checklistId))
            }

            ChecklistMainEvent.ToggleDrawer -> {
                var drawerState = false
                _state.update {
                    val innerDrawerState = !it.isDrawerOpen
                    // Assign this to the variable outside
                    drawerState = innerDrawerState

                    it.copy(
                        isDrawerOpen = innerDrawerState,
                    )
                }

                // Close the Action Menu if the Drawer is open. It's mandatory to close it
                if (_state.value.isActionMenuOpen) onEvent(ChecklistMainEvent.ToggleActionMenu(null))

                // Resets both the new and editing checklist if the drawer is closed
                // It's okay to reset both
                if (!drawerState) {
                    onEvent(ChecklistMainEvent.ResetNewChecklist)
                    onEvent(ChecklistMainEvent.ResetEditingChecklist)
                }
            }

            ChecklistMainEvent.ToggleIconPicker -> {
                _state.update { it.copy(isIconPickerOpen = !it.isIconPickerOpen) }
            }

            is ChecklistMainEvent.ToggleActionMenu -> {
                _state.update {
                    val newMenuState = !it.isActionMenuOpen
                    it.copy(
                        isActionMenuOpen = newMenuState,
                        selectedChecklist = if (newMenuState) event.checklist else null, // Set the selected checklist. If closed, set to null again
                    )
                }
            }

            is ChecklistMainEvent.ToggleDeleteDialog -> {

                var deleteDialogState = false
                _state.update {
                    val innerDeleteDialogState = !it.isDeleteDialogOpen
                    // Assign this to the variable outside
                    deleteDialogState = innerDeleteDialogState

                    it.copy(
                        isDeleteDialogOpen = innerDeleteDialogState,
                    )
                }

                // Close the Action Menu if the Dialog is open. It's mandatory to close it
                if (_state.value.isActionMenuOpen) onEvent(ChecklistMainEvent.ToggleActionMenu(null))

                // Resets both deletable checklist if the dialog is closed
                if (!deleteDialogState) {
                    onEvent(ChecklistMainEvent.ResetDeletingChecklist)
                }
            }

            is ChecklistMainEvent.SearchChecklist -> {

                _state.update {
                    it.copy(searchQuery = event.query) // Update the search query state for the UI)
                }

                // Cancels the previous search job if it exists
                searchJob?.cancel()

                // Create a new job
                searchJob = viewModelScope.launch {
                    delay(500)
                    // If the user is done searching, reload the checklists
                    if (event.query.isEmpty()) loadChecklists()

                    // Filter through the checklist state
                    val filteredChecklists = _state.value.checklists.filter { checklist ->
                        checklist.name.contains(event.query, ignoreCase = true)
                    }

                    // Update the checklist state with the filtered checklists
                    _state.update {
                        it.copy(
                            checklists = filteredChecklists,
                        )
                    }
                }
            }

            is ChecklistMainEvent.SetNewChecklist -> {
                _state.update { it.copy(newChecklist = event.checklist) }
            }

            is ChecklistMainEvent.SetDeletingChecklist -> {
                _state.update {
                    it.copy(deletingChecklist = event.checklist)
                }
            }

            is ChecklistMainEvent.SetEditingChecklist -> {
                _state.update {
                    it.copy(editingChecklist = event.checklist)
                }
            }

            ChecklistMainEvent.ResetNewChecklist -> {
                _state.update {
                    it.copy(
                        newChecklist = ChecklistInput(
                            name = "",
                            description = "",
                            icon = IconOption.MAIN_GROCERY,
                            iconBackgroundColor = ColorOption.CopySkyGreen
                        )
                    )
                }
            }

            ChecklistMainEvent.ResetEditingChecklist -> {
                _state.update {
                    it.copy(
                        editingChecklist = null
                    )
                }
            }

            ChecklistMainEvent.ResetDeletingChecklist -> {
                _state.update {
                    it.copy(deletingChecklist = null)
                }
            }

            is ChecklistMainEvent.AddChecklist -> {
                viewModelScope.launch {
                    try {
                        // Save the checklist to Room
                        checklistRepository.addChecklist(event.checklist)

                        // Close the drawer and reset the state of the newChecklist variable
                        onEvent(ChecklistMainEvent.ToggleDrawer)

                        // Reload the checklists
                        loadChecklists()
                    } catch (e: Exception) {
                        Log.e("ChecklistMainViewModel", "Error adding checklist: ${e.message}")
                    }
                }
            }

            is ChecklistMainEvent.DeleteChecklist -> {
                viewModelScope.launch {
                    try {
                        if (event.checklist?.id == null) return@launch
                        // Delete the item
                        checklistRepository.deleteChecklist(event.checklist)

                        // Close the Delete Dialog and reset the states
                        onEvent(ChecklistMainEvent.ToggleDeleteDialog)

                        loadChecklists()
                    } catch (e: Exception) {
                        Log.e("ChecklistMainViewModel", "Error deleting checklist: ${e.message}")
                    }
                }
            }

            is ChecklistMainEvent.UpdateChecklist -> {
                viewModelScope.launch {
                    try {
                        // Transform the checklist to ChecklistInput
                        val transformedChecklist = ChecklistInput(
                            name = event.checklist.name,
                            description = event.checklist.description,
                            icon = event.checklist.icon,
                            iconBackgroundColor = event.checklist.iconBackgroundColor
                        )

                        // Update the checklist
                        checklistRepository.updateChecklist(
                            event.checklist.id,
                            transformedChecklist
                        )

                        // Close the drawer and reset states
                        onEvent(ChecklistMainEvent.ToggleDrawer)

                        // Reload the checklists
                        loadChecklists()
                    } catch (e: Exception) {
                        Log.e("ChecklistMainViewModel", "Error updating checklist: ${e.message}")
                    }
                }
            }
        }
    }

    private fun loadChecklists() {
        viewModelScope.launch {
            checklistRepository.getChecklists().catch {
                emit(emptyList<Checklist>())
            }.collect { checklists ->
                _state.update { it.copy(checklists = checklists) }
            }
        }
    }
}