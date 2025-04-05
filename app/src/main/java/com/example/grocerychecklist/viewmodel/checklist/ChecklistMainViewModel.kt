package com.example.grocerychecklist.viewmodel.checklist

import android.R.id.input
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes.*
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent.*
import com.example.grocerychecklist.viewmodel.util.SubmissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.contains
import kotlin.time.Duration.Companion.milliseconds

class ChecklistMainViewModel(
    private val navigator: Navigator,
    private val repository: ChecklistRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChecklistMainState())
    val state: StateFlow<ChecklistMainState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private var _data: Flow<List<Checklist>> = repository.getChecklists()
    val uiState: StateFlow<ChecklistMainUIState> = createUIState(
        _data,
        _searchQuery
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistMainUIState.Loading
    )


    fun createUIState(
        checklists: Flow<List<Checklist>>,
        searchQuery: StateFlow<String>,
    ): Flow<ChecklistMainUIState> {
        return combine(checklists, searchQuery, ::Pair)
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val checklists = result.data.first
                        val query = result.data.second
                        when {
                            query.isEmpty() -> ChecklistMainUIState.Success(checklists)
                            checklists.isEmpty() -> ChecklistMainUIState.Empty
                            else -> ChecklistMainUIState.Success(checklists.filter { it.name.contains(searchQuery.value, ignoreCase = true) })
                        }
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Failed to load checklists: ${result.exception}")
                        ChecklistMainUIState.Error("Failed to load checklists")
                    }
                    is Result.Loading -> ChecklistMainUIState.Loading
                }
            }
    }

    fun loadData() {
        viewModelScope.launch {
            _data = repository.getChecklists()
        }
    }

    init {
        loadData()
    }

    fun onEvent(event: ChecklistMainEvent) {
        when (event) {
            is NavigateChecklist -> navigator.navigate(ChecklistDetail(event.checklistId, event.checklistName))
            is ToggleIconPicker -> toggleIconPicker()
            is SetEditingChecklist -> setEditingChecklist(event.checklist)
            is SetNewChecklist -> setNewChecklist(event.checklist)
            is AddChecklist -> addData(event.input)
            is DeleteChecklist -> deleteData(event.checklist)
            is UpdateChecklist -> updateData(event.checklist)
            is LoadChecklists -> loadData()
            is SearchQueryEvent -> updateSearch(event.query)
            is ToggleDeleteDialog -> toggleDeleteDialog()
            is ToggleDrawer -> toggleDrawer()

        }
    }
    fun toggleDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogOpen = !it.isDeleteDialogOpen,
            )
        }
    }

    fun toggleDrawer() {
        viewModelScope.launch {
            var wasOpen = false
            _state.update {
                wasOpen = it.isDrawerOpen
                it.copy(
                    isDrawerOpen = !wasOpen,
                )
            }

            delay(500.milliseconds)

            _state.update {
                it.copy(
                    // Only reset state when closing the drawer (wasOpen was true)
                    editingChecklist = if (wasOpen) null else it.editingChecklist,
                    newChecklist = if (wasOpen) ChecklistInput(
                        name = "",
                        description = ""
                    ) else it.newChecklist
                )
            }
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.update { query }
    }

    fun updateSubmissionState(state: SubmissionState) {
        _state.update {
            it.copy(submissionState = state)
        }
    }

    private fun toggleIconPicker() {
        _state.update {
            it.copy(isIconPickerOpen = !it.isIconPickerOpen)
        }
    }

    fun addData(input: ChecklistInput) {
        viewModelScope.launch {
                handleRepositoryResult(
                    result = repository.addChecklist(input),
                    errorMessage = "Failed to add checklist",
                    onSuccess = {
                        setNewChecklist(
                            ChecklistInput(
                                name = "",
                                description = ""
                            )
                        )

                        toggleDrawer()
                    }
                )
            }
    }

    fun updateData(checklist: Checklist) {
        viewModelScope.launch {
                handleRepositoryResult(
                    result = repository.updateChecklist(
                        checklist.id,
                        ChecklistInput(
                            checklist.name,
                            checklist.description,
                            checklist.icon,
                            checklist.iconBackgroundColor
                        )
                    ),
                    errorMessage = "Failed to update checklist",
                    onSuccess = {
                        setEditingChecklist(null)
                        toggleDrawer()
                    }
                )
            }
    }

    fun deleteData(toDelete: Checklist) {
        viewModelScope.launch {
                handleRepositoryResult(
                    result = repository.deleteChecklist(toDelete),
                    errorMessage = "Failed to delete checklist",
                    onSuccess = {
                        setEditingChecklist(null)
                        toggleDeleteDialog()
                        toggleDrawer()
                    }
                )
            }
    }

    private fun setNewChecklist(checklist: ChecklistInput?) {
        _state.update {
            it.copy(
                newChecklist = checklist?: ChecklistInput(
                    name = "",
                    description = ""
                )
            )
        }
    }


    private fun setEditingChecklist(checklist: Checklist?) {
        _state.update {
            it.copy(
                editingChecklist = checklist
            )
        }
    }

    fun handleRepositoryResult(
        result: Result<*>,
        errorMessage: String,
        onSuccess: (() -> Unit)? = null
    ) {
        when (result) {
            is Result.Error -> {
                Log.e(TAG, "$errorMessage: ${result.exception}")
                updateSubmissionState(SubmissionState.Error(errorMessage))
            }
            is Result.Success -> {
                onSuccess?.invoke()
                loadData()
                updateSubmissionState(SubmissionState.Success)
            }
            is Result.Loading -> updateSubmissionState(SubmissionState.Loading)
        }
    }

    companion object{
        private const val TAG = "ChecklistMainVM"
    }
}