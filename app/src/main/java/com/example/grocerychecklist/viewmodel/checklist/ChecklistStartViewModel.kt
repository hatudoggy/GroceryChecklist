package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.checklist.FilterType
import com.example.grocerychecklist.viewmodel.util.SubmissionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChecklistItemFormInputs (
    val name: String,
    val category: ItemCategory,
    val price: Double,
    val quantity: Int,
)

class ChecklistStartViewModel(
    private val checklistId: Long,
    private val checklistName: String,
    private val navigator: Navigator,
    private val checklistItemRepository: ChecklistItemRepository,
    private val checklistRepository: ChecklistRepository,
    private val historyRepository: HistoryRepository,
    private val historyItemRepository: HistoryItemRepository,
) : ViewModel() {
    val _state: MutableStateFlow<ChecklistStartState> = MutableStateFlow(ChecklistStartState())
    val state: StateFlow<ChecklistStartState> = _state.asStateFlow()

    val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val _selectedChip = MutableStateFlow(FilterType.ALL)
    val selectedChip = _selectedChip.asStateFlow()

    var _data = checklistDataMapper(checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Name))
    val uiState: StateFlow<ChecklistStartUIState> = createUIState(
        _data,
        _searchQuery,
        _selectedChip
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistStartUIState.Loading
    )

    fun loadData() {
        viewModelScope.launch {
            _data = checklistDataMapper(checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Name))
        }
    }

    fun createUIState(
        items: Flow<List<ChecklistItemData>>,
        searchQuery: StateFlow<String>,
        filterType: StateFlow<FilterType>
    ): Flow<ChecklistStartUIState> {
        return combine(items, searchQuery, filterType, ::Triple)
            .asResult()
            .map { result ->
                when(result){
                    is Result.Success -> {
                        val items = result.data.first
                        val query = result.data.second
                        val type = result.data.third
                        when {
                            query.isEmpty() -> ChecklistStartUIState.Success(items)
                            items.isEmpty() -> ChecklistStartUIState.Empty
                            else -> ChecklistStartUIState.Success(
                                items.filter {
                                    it.name.contains(searchQuery.value, ignoreCase = true)
                                    when(type) {
                                        FilterType.ALL -> true
                                        FilterType.CHECKED -> it.isChecked
                                        FilterType.UNCHECKED -> !it.isChecked
                                    }
                                }
                            )
                        }
                    }
                    is Result.Error -> ChecklistStartUIState.Error("Failed to load checklists")
                    is Result.Loading -> ChecklistStartUIState.Loading
                }
            }
    }

    init {
        _state.update {it.copy(checklistName = checklistName)}
        loadData()
    }

    fun onEvent(event: ChecklistStartEvent){
        when (event) {
            is ChecklistStartEvent.NavigateBack -> navigator.popBackStack()

            is ChecklistStartEvent.ToggleDrawer -> toggleDrawer()
            is ChecklistStartEvent.ToggleDeleteDialog -> toggleDeleteDialog()
            is ChecklistStartEvent.ToggleActionMenu-> toggleActionMenu(event.checklist)
            is ChecklistStartEvent.ToggleCheckout -> toggleCheckout()
            is ChecklistStartEvent.ToggleItemCheck -> toggleItemCheck(event.checklistItemId)
            is ChecklistStartEvent.ProceedCheckout -> proceedCheckout()

            is ChecklistStartEvent.SearchQueryEvent -> updateSearch(event.query)
            is ChecklistStartEvent.ItemSelection -> updateSelectedItem(event.checklist)
            is ChecklistStartEvent.FilterSelection -> updateSelectedChip(event.type)

            is ChecklistStartEvent.ItemAddition -> addData(event.input)
            is ChecklistStartEvent.ItemModification -> editData(event.checklistItemId, event.input)
            is ChecklistStartEvent.ItemDeletion -> deleteData(event.checklistItemId, event.groceryItemId)
            is ChecklistStartEvent.LoadData -> loadData()
        }
    }


    fun toggleDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogOpen = !it.isDeleteDialogOpen,
                isActionMenuOpen = false,
                selectedItem = null
            ) 
        }
    }

    fun toggleDrawer() {
        _state.update {
            it.copy(
                isDrawerOpen = !it.isDrawerOpen,
                isActionMenuOpen = false,
                selectedItem = null
            )
        }
    }

    fun toggleActionMenu(item: ChecklistItemData) {
        _state.update {
            it.copy(
                isActionMenuOpen = !it.isActionMenuOpen,
                selectedItem = item
            )
        }
    }

    fun toggleCheckout(){
        _state.update { it.copy(
            isCheckoutOpen = !it.isCheckoutOpen,
            isActionMenuOpen = false,
            isDeleteDialogOpen = false,
            isDrawerOpen = false,
            selectedItem = null
        ) }
    }

    fun toggleItemCheck(checklistItemId: Long){
        _data.map { list ->
            list.map {
                if (it.id == checklistItemId) {
                    it.copy(isChecked = !it.isChecked)
                } else {
                    it
                }
            }
        }
    }

    fun updateSelectedItem(item : ChecklistItemData?) {
        _state.update {
            it.copy(selectedItem = item)
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.update { query }
    }

    fun updateSelectedChip(type: FilterType) {
        _selectedChip.update { type }
    }

    fun updateSubmissionState(state: SubmissionState) {
        _state.update {
            it.copy(submissionState = state)
        }
    }

    fun proceedCheckout() {
        viewModelScope.launch {
            try {
                updateSubmissionState(SubmissionState.Loading)

                val checklist = checklistRepository.getChecklist(checklistId).first()

                if (checklist == null) {
                    updateSubmissionState(SubmissionState.Error("Checklist not found"))
                    return@launch
                }

                val historyId = historyRepository.addHistory(checklist)
                val mapChecked = _data.first().filter { it.isChecked }

                handleRepositoryResult(
                    historyItemRepository.addHistoryItems(
                        historyId, mapChecked
                    ),
                    "Failed to add history items",
                    onSuccess = { toggleCheckout() }
                )

            } catch (err: Error) {
                Log.e(TAG, "Error checking out: ${err.message}")
                updateSubmissionState(SubmissionState.Error("Failed to check out"))
            }
        }
    }

    fun addData(input: ChecklistItemFormInputs){
        viewModelScope.launch {
            try {
                updateSubmissionState(SubmissionState.Loading)

                val item = ChecklistItemInput(
                    name = input.name,
                    price = input.price,
                    quantity = input.quantity,
                    category = input.name,
                    measureType = "",
                    measureValue = 0.00,
                    photoRef = ""
                )

                handleRepositoryResult(
                    checklistItemRepository.addChecklistItem(checklistId, item),
                    "Error adding item to checklist"
                ) {
                    toggleDrawer()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding item to checklist: $e")
                updateSubmissionState(SubmissionState.Error("Error while adding item to checklist"))
            }
        }
    }
    fun editData(checklistItemId: Long, input: ChecklistItemFormInputs){
        viewModelScope.launch {
            try {
                updateSubmissionState(SubmissionState.Loading)
                val item = ChecklistItemInput(
                    name = input.name,
                    price = input.price,
                    quantity = input.quantity,
                    category = input.name,
                    measureType = "",
                    measureValue = 0.00,
                    photoRef = ""
                )
                handleRepositoryResult(
                    checklistItemRepository.updateChecklistItem(checklistItemId, item),
                    "Error editing item in checklist"
                ) {
                    toggleDrawer()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating item in checklist: $e")
                updateSubmissionState(SubmissionState.Error("Error while updating item in checklist"))
            }
        }
    }
    fun deleteData(checklistItemId : Long, groceryItemId : Long?){
        viewModelScope.launch {
            try {
                updateSubmissionState(SubmissionState.Loading)
                handleRepositoryResult(
                    if (groceryItemId == 0L || groceryItemId == null){
                        checklistItemRepository.deleteChecklistItem(checklistItemId)
                    } else {
                        checklistItemRepository.deleteChecklistItemAndItem(checklistItemId, groceryItemId)
                    },
                    "Error removing item from checklist"
                ) {
                    toggleDeleteDialog()
                }
                } catch (e: Exception) {
                Log.e(TAG, "Error removing item from checklist: $e")
                updateSubmissionState(SubmissionState.Error("Error while removing item from checklist"))
            }
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
        private const val TAG = "ChecklistStartVM"
    }
}