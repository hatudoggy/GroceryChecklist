package com.example.grocerychecklist.viewmodel.checklist

import ItemCategory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.Result
import com.example.grocerychecklist.data.repository.asResult
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.checklist.ChecklistMode
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
import java.io.Serializable
import kotlin.collections.first
import kotlin.collections.map

data class ChecklistItemFormInputs (
    val name: String,
    val category: String,
    val price: Double,
    val quantity: Int,
)

class ChecklistStartViewModel(
    private val checklistId: Long,
    private val checklistName: String,
    private val filterByCategory: ItemCategory,
    private val mode: ChecklistMode,
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

    var _data = MutableStateFlow<List<ChecklistItemData>>(emptyList())
    val uiState: StateFlow<ChecklistStartUIState> = createUIState(
        _data,
        _searchQuery
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChecklistStartUIState.Loading
    )

    fun loadData() {
        viewModelScope.launch {
            // Get the raw data first
            val rawData = checklistDataMapper(
                checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Order)
            ).first()

            // Apply filters
            val filteredData = if (_state.value.selectedCategories.contains(ItemCategory.ALL)) {
                rawData
            } else {
                rawData.filter { item ->
                    _state.value.selectedCategories.contains(item.category)
                }
            }

            // Apply sorting
            val sortedData = when (_state.value.selectedSortOption) {
                ChecklistItemOrder.Name -> filteredData.sortedBy { it.name }
                ChecklistItemOrder.Category -> filteredData.sortedBy { it.category.name }
                ChecklistItemOrder.Price -> filteredData.sortedBy { it.price }
                ChecklistItemOrder.Quantity -> filteredData.sortedBy { it.quantity }
                ChecklistItemOrder.Date -> filteredData.sortedBy { it.createdAt }
                ChecklistItemOrder.Order -> filteredData
            }.let { sortedList ->
                if (_state.value.isSortAscending) sortedList else sortedList.reversed()
            }

            _data.value = sortedData
        }
    }

    fun createUIState(
        items: Flow<List<ChecklistItemData>>,
        searchQuery: StateFlow<String>,
    ): Flow<ChecklistStartUIState> {
        return combine(items, searchQuery) { items, query ->
            items.filter { item ->
                val matchesSearch = query.isEmpty() ||
                        item.name.contains(query, ignoreCase = true)

                matchesSearch
            }
        }
            .asResult()
            .map { result ->
                when(result) {
                    is Result.Success -> {
                        val filteredItems = result.data
                        when {
                            filteredItems.isEmpty() && searchQuery.value.isNotEmpty() ->
                                ChecklistStartUIState.Empty
                            filteredItems.isEmpty() -> ChecklistStartUIState.Empty
                            else -> ChecklistStartUIState.Success(filteredItems)
                        }
                    }
                    is Result.Error -> ChecklistStartUIState.Error("Failed to load checklists")
                    is Result.Loading -> ChecklistStartUIState.Loading
                }
            }
    }

    init {
        _state.update {
            it.copy(
                checklistName = checklistName,
                mode = mode,
                selectedCategories = setOf(filterByCategory)
            )
        }
        loadData()
    }

    fun onEvent(event: ChecklistStartEvent){
        when (event) {
            is ChecklistStartEvent.NavigateBack -> navigator.popBackStack()

            is ChecklistStartEvent.ToggleDrawer -> toggleDrawer()
            is ChecklistStartEvent.ToggleDeleteDialog -> toggleDeleteDialog()
            is ChecklistStartEvent.ToggleActionMenu-> toggleActionMenu(event.checklist)
            is ChecklistStartEvent.ToggleCheckout -> toggleCheckout()
            is ChecklistStartEvent.ToggleItemCheck -> toggleItemCheck(event.checklistItem)
            is ChecklistStartEvent.ProceedCheckout -> proceedCheckout()

            is ChecklistStartEvent.SearchQueryEvent -> updateSearch(event.query)
            is ChecklistStartEvent.FilterSelection -> updateSelectedChip(event.type)

            is ChecklistStartEvent.ItemAddition -> addData(event.input)
            is ChecklistStartEvent.ItemModification -> editData(event.checklistItemId, event.input)
            is ChecklistStartEvent.ItemDeletion -> deleteData(event.checklistItemId, event.groceryItemId)
            is ChecklistStartEvent.ChangeMode -> {
                _state.update { it.copy(
                    mode = event.mode,
                    isSelectionModeActive = false,
                    selectedChip = FilterType.ALL,
                    selectedItems = emptyList(),
                    checkedItems = emptyList(),
                    selectedCategories = setOf(ItemCategory.ALL),
                ) }

                loadData()
            }

            is ChecklistStartEvent.ToggleItemSelection -> {
                viewModelScope.launch {
                    _state.update { state ->
                        val newSelection = if (state.selectedItems.contains(event.itemId)) {
                            state.selectedItems - event.itemId
                        } else {
                            state.selectedItems + event.itemId
                        }

                        state.copy(
                            selectedItems = newSelection,
                            isSelectionModeActive = true,
                        )
                    }

                    _state.update { state ->
                        val editingItem = if (state.selectedItems.size == 1 ){
                            _data.first().find { it.id == state.selectedItems.first() }
                        } else {
                            null
                        }

                        state.copy(
                            editingItem = editingItem
                        )
                    }
                }
            }

            is ChecklistStartEvent.MoveSelectedItems -> {
                viewModelScope.launch {
                    state.value.selectedItems.forEach { itemId ->
                        checklistItemRepository.updateChecklistItemCategory(
                            itemId,
                            event.newCategory.name
                        )
                    }
                    loadData()
                    _state.update { it.copy(
                        isSelectionModeActive = false,
                        selectedItems = emptyList()
                    ) }
                }
            }

            is ChecklistStartEvent.DeleteSelectedItems -> {
                viewModelScope.launch {
                    state.value.selectedItems.forEach { itemId ->
                        if (event.alsoDeleteGroceryItems) {
                            // Get grocery item ID first
                            val groceryItemId = checklistItemRepository
                                .getChecklistItem(itemId)
                                .first()?.item?.id
                            if (groceryItemId != null) {
                                checklistItemRepository
                                    .deleteChecklistItemAndItem(itemId, groceryItemId)
                            }
                        } else {
                            checklistItemRepository.deleteChecklistItem(itemId)
                        }
                    }
                    loadData()
                    _state.update { it.copy(
                        isSelectionModeActive = false,
                        selectedItems = emptyList()
                    ) }
                }
            }

            is ChecklistStartEvent.ChangeSortOption -> {
                _state.update { it.copy(selectedSortOption = event.option) }
                loadData() // Reload with new sort
            }
            is ChecklistStartEvent.ToggleSortDirection -> {
                _state.update { it.copy(isSortAscending = !it.isSortAscending) }
                loadData() // Reload with new sort direction
            }
            is ChecklistStartEvent.LoadData -> loadData()
            is ChecklistStartEvent.ClearSelection -> {
                _state.update { it.copy(
                    selectedItems = emptyList()
                ) }
            }
            is ChecklistStartEvent.SetEditingItem -> {
                _state.update { it.copy(editingItem = event.item) }
            }
            is ChecklistStartEvent.SelectAllItems -> {
                viewModelScope.launch { _state.update {
                    it.copy(selectedItems = _data.first().map { it.id })
                } }
            }
            is ChecklistStartEvent.ToggleChangeCategoryDialog -> toggleChangeCategoryDialog()
            is ChecklistStartEvent.ToggleMoreActionsMenu -> toggleMoreActionsMenu()
            is ChecklistStartEvent.ChangeCategory -> changeSelectedItemsCategory(event.items, event.newCategory)
            is ChecklistStartEvent.ChangeItemSelectMode -> { _state.update { it.copy(isSelectionModeActive = event.mode) } }
            is ChecklistStartEvent.ToggleCopyDialog -> toggleCopyDialog()
            is ChecklistStartEvent.CopyToNewChecklist -> copyItemsToNewChecklist(event.checklistInput)
            is ChecklistStartEvent.ClearAllFilters -> clearFilters()
            is ChecklistStartEvent.ToggleCategorySelection -> toggleCategorySelection(event.category)
            is ChecklistStartEvent.ToggleFilterBottomSheet -> toggleFilterBottomSheet()
            is ChecklistStartEvent.SetNewChecklist -> { _state.update { it.copy(newChecklist = event.checklist) }}
            ChecklistStartEvent.ToggleIconPicker -> toggleIconPicker()
        }
    }

    private fun clearFilters() {
        _state.update {
            it.copy(
                selectedCategories = setOf(ItemCategory.ALL),
                selectedSortOption = ChecklistItemOrder.Name,
                isSortAscending = true
            )
        }
        loadData()
    }

    private fun toggleIconPicker() {
        _state.update {
            it.copy(
                isIconPickerOpen = !it.isIconPickerOpen
            )
        }
    }

    private fun toggleCategorySelection(category: ItemCategory) {
        _state.update { state ->
            val newSelection = state.selectedCategories.toMutableSet()
            if (category == ItemCategory.ALL) {
                newSelection.clear()
                newSelection.add(ItemCategory.ALL)
            } else {
                newSelection.remove(ItemCategory.ALL)
                if (newSelection.contains(category)) {
                    newSelection.remove(category)
                    if (newSelection.isEmpty()) {
                        newSelection.add(ItemCategory.ALL)
                    }
                } else {
                    newSelection.add(category)
                }
            }
            state.copy(selectedCategories = newSelection)
        }
        loadData()
    }

    private fun toggleFilterBottomSheet() {
        _state.update {
            it.copy(
                isFilterBottomSheetOpen = !it.isFilterBottomSheetOpen,
                isActionMenuOpen = false,
            )
        }
    }

    private fun toggleCopyDialog() {
        _state.update {
            it.copy(
                isCopyDialogOpen = !it.isCopyDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    private fun toggleMoreActionsMenu() {
        _state.update {
            it.copy(
                isMoreActionsMenuOpen = !it.isMoreActionsMenuOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    private fun toggleChangeCategoryDialog() {
        _state.update {
            it.copy(
                isChangeCategoryDialogOpen = !it.isChangeCategoryDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    fun toggleDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogOpen = !it.isDeleteDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    fun toggleDrawer() {
        Log.d(TAG, "toggleDrawer called")
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isDrawerOpen = !it.isDrawerOpen,
                    isActionMenuOpen = false,
                    editingItem = if (it.isDrawerOpen) null else it.editingItem
                )
            }
        }
        Log.d(TAG, "toggleDrawer completed with state: ${state.value}")
    }

    fun toggleActionMenu(item: ChecklistItemData) {
        _state.update {
            it.copy(
                isActionMenuOpen = !it.isActionMenuOpen,
                editingItem = item
            )
        }
    }

    fun toggleCheckout(){
        _state.update { it.copy(
            isCheckoutOpen = !it.isCheckoutOpen,
            isActionMenuOpen = false,
            isDeleteDialogOpen = false,
            isDrawerOpen = false,
            editingItem = null
        ) }
    }

    fun toggleItemCheck(checklistItem: ChecklistItemData) {
        _state.update { currentState ->
            val updatedList = currentState.checkedItems.toMutableList()
            if (currentState.checkedItems.any { it.id == checklistItem.id }) {
                updatedList.removeAll { it.id == checklistItem.id }
            } else {
                updatedList.add(checklistItem)
            }
            currentState.copy(checkedItems = updatedList)
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.update { query }
    }

    fun updateSelectedChip(type: FilterType) {
        _state.update {
            it.copy(selectedChip = type)
        }
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
                    ?: throw Exception("Checklist not found")

                val historyId = historyRepository.addHistory(checklist)
                val checkedItems = state.value.checkedItems

                if (checkedItems.isEmpty()) {
                    updateSubmissionState(SubmissionState.Error("No items selected"))
                    return@launch
                }

                handleRepositoryResult(
                    historyItemRepository.addHistoryItems(
                        historyId, checkedItems
                    ),
                    "Failed to add history items",
                    onSuccess = {
                        _state.update { it.copy(checkedItems = emptyList()) }
                        toggleCheckout()
                    }
                )

            } catch (err: Error) {
                Log.e(TAG, "Error checking out: ${err.message}")
                updateSubmissionState(SubmissionState.Error("Failed to check out"))
            }
        }
    }

    fun changeSelectedItemsCategory(items: List<Long>, category: ItemCategory) {
        viewModelScope.launch {
            try {
                items.forEach { itemId ->
                    handleRepositoryResult(
                        checklistItemRepository.updateChecklistItemCategory(itemId, category.name),
                        "Error changing selected items category",
                        onSuccess = {
                            _state.update { it.copy(selectedItems = emptyList()) }
                            toggleChangeCategoryDialog()
                            loadData()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error changing selected items category: $e")
                updateSubmissionState(SubmissionState.Error("Error while changing selected items category"))
            }
        }
    }

    fun copyItemsToNewChecklist(checklistInput: ChecklistInput) {
        viewModelScope.launch {
            try {
                val result = checklistRepository.addChecklist(checklistInput)

                if (result is Result.Error) {
                    updateSubmissionState(SubmissionState.Error("Error creating new checklist"))
                    return@launch
                }

                val checklistId = (result as Result.Success<Long>).data

                val items: Map<Long, Int> = state.value.selectedItems.associateWith {
                    state.value.checkedItems.find { item -> item.id == it }?.quantity ?: 0
                }

                handleRepositoryResult(
                    checklistItemRepository.copyChecklistItemsToChecklist(checklistId, items),
                    "Error copying item to new checklist",
                    onSuccess = {
                        _state.update { it.copy(selectedItems = emptyList()) }
                        toggleCopyDialog()
                        loadData()
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error copying item to new checklist: $e")
                updateSubmissionState(SubmissionState.Error("Error while copying item to new checklist"))
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
                    category = input.category,
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

        data class Quadruple<out A, out B, out C, out D>(
            val first: A,
            val second: B,
            val third: C,
            val fourth: D,
        ) : Serializable {

            /**
             * Returns string representation of the [Companion.Quadruple] including its [first], [second], [third], and [fourth] values.
             */
            override fun toString(): String = "($first, $second, $third, $fourth)"
        }
    }
}