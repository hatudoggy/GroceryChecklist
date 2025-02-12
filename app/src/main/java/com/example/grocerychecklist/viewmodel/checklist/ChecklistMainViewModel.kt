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

            is ChecklistMainEvent.ToggleActionMenu -> {
                _state.update {
                    val newMenuState = !it.isActionMenuOpen
                    it.copy(
                        isActionMenuOpen = newMenuState,
                        selectedChecklist = if (newMenuState) event.checklist else null
                    )
                }
            }

            is ChecklistMainEvent.ToggleDeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogOpen = !it.isDeleteDialogOpen
                    )
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

            is ChecklistMainEvent.UpdateChecklistName -> {
                _state.update { it.copy(newChecklist = it.newChecklist.copy(name = event.name)) }
            }

            is ChecklistMainEvent.UpdateChecklistDescription -> {
                _state.update { it.copy(newChecklist = it.newChecklist.copy(description = event.description)) }
            }

            is ChecklistMainEvent.UpdateChecklistIcon -> {
                _state.update {
                    it.copy(
                        newChecklist = it.newChecklist.copy(
                            icon = event.icon, iconBackgroundColor = event.color
                        )
                    )
                }
            }

            is ChecklistMainEvent.AddChecklist -> {
                viewModelScope.launch {
                    try {
                        checklistRepository.addChecklist(event.checklist)
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
                        // Close the Delete Dialog
                        onEvent(ChecklistMainEvent.ToggleDeleteDialog)

                        // Close the Action Menu
                        onEvent(ChecklistMainEvent.ToggleActionMenu(event.checklist))

                        // Delete the item
                        checklistRepository.deleteChecklist(event.checklist)
                        loadChecklists()
                    } catch (e: Exception) {
                        Log.e("ChecklistMainViewModel", "Error deleting checklist: ${e.message}")
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