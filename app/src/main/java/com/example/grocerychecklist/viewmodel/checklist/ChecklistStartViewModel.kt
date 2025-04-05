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

/**
 * ViewModel for the Checklist Start screen, responsible for managing checklist items,
 * filtering, sorting, checkout, and other user interactions within a checklist.
 *
 * @param checklistId The ID of the checklist being viewed.
 * @param checklistName The name of the checklist being viewed.
 * @param filterByCategory The initial category filter applied to the checklist items.
 * @param mode The current mode of the checklist (e.g., VIEW, EDIT).
 * @param navigator The navigator for handling screen transitions.
 * @param checklistItemRepository Repository for managing checklist items.
 * @param checklistRepository Repository for managing checklists.
 * @param historyRepository Repository for managing checkout history.
 * @param historyItemRepository Repository for managing items in checkout history.
 */
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

    /**
     * Loads checklist items, applies filtering and sorting based on the current state,
     * and updates the [_data] LiveData with the processed list.
     *
     * The process involves:
     * 1. Fetching raw checklist item data from the repository.
     * 2. Applying category filters based on the currently selected categories in the UI state.
     *    If "ALL" categories are selected, no filtering is applied.
     * 3. Sorting the filtered data based on the selected sorting option (e.g., Name, Category, Price)
     *    and sort order (ascending/descending) from the UI state.
     * 4. Updating the [_data] LiveData with the resulting sorted and filtered list.
     *
     * This function is launched within the viewModelScope to ensure it runs on the main thread
     * and can update LiveData safely.  It uses `checklistDataMapper` to transform the raw repository
     * data into the format used by the UI.
     */
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

    /**
     * Creates a UI state flow for the checklist start screen.
     *
     * This function combines a flow of checklist items and a state flow representing the search query
     * to produce a flow of [ChecklistStartUIState].  The UI state reflects the loading status, errors,
     * and the filtered list of checklist items based on the search query.
     *
     * @param items A flow of lists of [ChecklistItemData], representing the available checklist items.
     * @param searchQuery A state flow of strings, representing the current search query.
     * @return A flow of [ChecklistStartUIState], reflecting the current state of the checklist start screen.  Possible states are:
     *   - [ChecklistStartUIState.Loading]:  Indicates that the checklist items are being loaded.
     *   - [ChecklistStartUIState.Success]: Indicates that the checklist items have been loaded successfully.  Contains a list of filtered [ChecklistItemData] that match the search query.
     *   - [ChecklistStartUIState.Empty]: Indicates that no checklist items match the search query, or that there are no items available.
     *   - [ChecklistStartUIState.Error]: Indicates that an error occurred while loading the checklist items.  Contains an error message.
     */
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

    /**
     * Handles events related to the checklist, triggering corresponding actions within the ViewModel.
     *
     * This function uses a `when` statement to determine the type of event and execute the appropriate logic.
     *  It interacts with the UI state (`_state`), the checklist item repository, and navigation.
     *
     * @param event The [ChecklistStartEvent] to be processed.  Each event subtype represents a specific user action or state change.
     *
     *  **Event Types:**
     *
     *  - **Navigation:**
     *      - `NavigateBack`: Navigates the user back to the previous screen.
     *
     *  - **UI Toggles:**
     *      - `ToggleDrawer`: Opens or closes the application drawer.
     *      - `ToggleDeleteDialog`: Shows or hides the delete confirmation dialog.
     *      - `ToggleActionMenu`: Shows or hides the action menu associated with a checklist.
     *      - `ToggleCheckout`:  Toggles the visibility of the checkout interface.
     *      - `ToggleItemCheck`:  Marks a checklist item as checked or unchecked.
     *      - `ToggleItemSelection`:  Selects or deselects an item for bulk actions.
     *      - `ToggleChangeCategoryDialog`: Shows or hides the dialog for changing item categories.
     *      - `ToggleMoreActionsMenu`: Shows or hides a menu with additional actions.
     *      - `ToggleCopyDialog`: Shows or hides the dialog for copying items to a new checklist.
     *      - `ToggleFilterBottomSheet`: Shows or hides the bottom sheet for filtering options.
     *      - `ToggleIconPicker`: Shows or hides the icon picker dialog
     *
     *  - **Checkout:**
     *      - `ProceedCheckout`: Initiates the checkout process.
     *
     *  - **Search and Filtering:**
     *      - `SearchQueryEvent`: Updates the search query string.
     *      - `FilterSelection`: Applies a selected filter type.
     *      - `ClearAllFilters`: Removes all active filters.
     *      - `ToggleCategorySelection`: Selects or deselects an item category for filtering.
     *
     *  - **Item Management:**
     *      - `ItemAddition`: Adds a new checklist item.
     */
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

    /**
     * Clears the currently applied filters and resets the sorting options to their default values.
     *
     * This function updates the UI state to reflect the cleared filters and then reloads the data
     * with the default filtering and sorting settings.  Specifically, it resets the selected
     * categories to include only "ALL", the sort option to "Name", and the sort order to ascending.
     */
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

    /**
     * Toggles the visibility of the icon picker.
     *
     * This function updates the current UI state by inverting the `isIconPickerOpen` flag.
     * If the icon picker is currently open, it will be closed, and vice versa.
     */
    private fun toggleIconPicker() {
        _state.update {
            it.copy(
                isIconPickerOpen = !it.isIconPickerOpen
            )
        }
    }

    /**
     * Toggles the selection state of a given item category.
     *
     * This function manages the selection of item categories, ensuring that only a valid set of categories is selected at any time.
     * It uses a `MutableSet` to represent the currently selected categories, where `ItemCategory.ALL` has a special meaning:
     *
     * - If `category` is `ItemCategory.ALL`, it clears all other selections and selects only `ItemCategory.ALL`.
     * - Otherwise, it first removes `ItemCategory.ALL` (if present). Then:
     *   - If `category` is already selected, it is removed. If removing it leaves the set empty, `ItemCategory.ALL` is added back.
     *   - If `category` is not selected, it is added to the set.
     *
     * After updating the selection state in the [_state] object, it calls [loadData] to refresh the displayed data based on the new selection.
     *
     * @param category The [ItemCategory] to toggle the selection state for.
     */
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

    /**
     * Toggles the visibility of the filter bottom sheet.
     *
     * When called, this function updates the UI state to reflect the change in visibility of the filter bottom sheet.
     * It also ensures that the action menu is closed if the filter bottom sheet is being opened.
     *
     * The function updates the [UiState] using [MutableStateFlow.update] and a lambda that takes the current state as input and returns a modified copy of the state.
     *
     * The updated state includes:
     * - `isFilterBottomSheetOpen`: The boolean value indicating whether the filter bottom sheet is currently open. This value is toggled with each call to this function.
     * - `isActionMenuOpen`: The boolean value indicating whether the action menu is currently open.  This is set to false to ensure only one bottom sheet is open at a time.
     */
    private fun toggleFilterBottomSheet() {
        _state.update {
            it.copy(
                isFilterBottomSheetOpen = !it.isFilterBottomSheetOpen,
                isActionMenuOpen = false,
            )
        }
    }

    /**
     * Toggles the visibility of the copy dialog.
     *
     * When the dialog is toggled open, the action menu is closed and any editing item is cleared.
     * When the dialog is toggled closed, no other state changes occur.
     */
    private fun toggleCopyDialog() {
        _state.update {
            it.copy(
                isCopyDialogOpen = !it.isCopyDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    /**
     * Toggles the visibility of the "More Actions" menu.  When opened, it closes the main action menu and cancels any ongoing item editing.
     */
    private fun toggleMoreActionsMenu() {
        _state.update {
            it.copy(
                isMoreActionsMenuOpen = !it.isMoreActionsMenuOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    /**
     * Toggles the visibility of the change category dialog.
     * When the dialog is opened, it also closes the action menu and clears the currently editing item.
     */
    private fun toggleChangeCategoryDialog() {
        _state.update {
            it.copy(
                isChangeCategoryDialogOpen = !it.isChangeCategoryDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    /**
     * Toggles the visibility of the delete confirmation dialog.
     *
     * This function updates the UI state to either show or hide the delete dialog.
     *  It also closes the action menu and clears any editing item, ensuring that
     *  only one dialog (delete or edit) is open at a time.
     */
    fun toggleDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogOpen = !it.isDeleteDialogOpen,
                isActionMenuOpen = false,
                editingItem = null
            )
        }
    }

    /**
     * Toggles the drawer's visibility state.
     *
     * When the drawer is opened, it also closes the action menu and clears any item being edited.
     * When the drawer is closed, it keeps the editing item as it was.
     * The state updates are performed asynchronously within the viewModelScope.
     *
     * Logs the start and completion of the operation, including the resulting state.
     */
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

    /**
     * Toggles the visibility of the action menu for a specific checklist item.
     *
     * When called, this function updates the UI state to either show or hide the action menu.
     * It also associates the currently selected [ChecklistItemData] with the menu,
     * allowing actions performed within the menu to target that specific item.
     *
     * @param item The [ChecklistItemData] associated with the action menu being toggled.
     *           This item will be used as the target for any actions performed within the menu.
     */
    fun toggleActionMenu(item: ChecklistItemData) {
        _state.update {
            it.copy(
                isActionMenuOpen = !it.isActionMenuOpen,
                editingItem = item
            )
        }
    }

    /**
     * Toggles the visibility of the checkout UI.
     *
     * This function updates the state to either open or close the checkout UI.  When opening or closing the checkout,
     * it also ensures that other UI elements, such as the action menu, delete dialog, and drawer, are closed,
     * and any ongoing item editing is cancelled.
     */
    fun toggleCheckout(){
        _state.update { it.copy(
            isCheckoutOpen = !it.isCheckoutOpen,
            isActionMenuOpen = false,
            isDeleteDialogOpen = false,
            isDrawerOpen = false,
            editingItem = null
        ) }
    }

    /**
     * Toggles the checked state of a checklist item.
     *
     * If the item is currently checked (present in the `checkedItems` list), it will be removed, effectively unchecking it.
     * If the item is currently unchecked (not present in the `checkedItems` list), it will be added, effectively checking it.
     *
     * @param checklistItem The checklist item whose checked state needs to be toggled.  The `id` property of this item is used to identify it within the list of checked items.
     */
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

    /**
     * Updates the search query.
     *
     * @param query The new search query string.
     */
    fun updateSearch(query: String) {
        _searchQuery.update { query }
    }

    /**
     * Updates the currently selected filter chip.
     *
     * @param type The [FilterType] representing the chip that should be selected.  This will
     *             update the [FilterState.selectedChip] property in the UI state.
     */
    fun updateSelectedChip(type: FilterType) {
        _state.update {
            it.copy(selectedChip = type)
        }
    }

    /**
     * Updates the current submission state of the UI.
     *
     * This function updates the `submissionState` within the internal state holder (`_state`) by creating a copy of the existing state and setting the new `submissionState`.  This ensures that state updates are handled immutably and trigger UI recomposition.
     *
     * @param state The new [SubmissionState] to be set.  This likely represents the different stages of a submission process (e.g., `Idle`, `Loading`, `Success`, `Error`).
     */
    fun updateSubmissionState(state: SubmissionState) {
        _state.update {
            it.copy(submissionState = state)
        }
    }

    /**
     * Proceeds with the checkout process.
     *
     * This function performs the following steps:
     *  1. Sets the submission state to [SubmissionState.Loading].
     *  2. Retrieves the checklist from the repository using [checklistId].
     *  3. If the checklist is not found, sets the submission state to [SubmissionState.Error] with "Checklist not found" and returns.
     *  4. Adds a history record to the repository using the retrieved checklist.
     *  5. Retrieves the currently checked items from the state.
     *  6. If no items are selected, sets the submission state to [SubmissionState.Error] with "No items selected" and returns.
     *  7. Attempts to add the checked items to the history record in the repository using [historyId].
     *  8. If successful:
     *      - Clears the checked items in the state.
     *      - Toggles the checkout state (likely hiding the checkout UI).
     *  9. If unsuccessful, sets the submission state to [SubmissionState.Error] with a message indicating failure to add history items.
     *  10. In case of any other errors during the process (e.g., network issues, repository failures), logs the error and sets the submission state to [SubmissionState.Error] with "Failed to check out".
     */
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

    /**
     * Changes the category of a list of selected checklist items.
     *
     * This function iterates through the provided list of item IDs and updates their category in the repository.
     * Upon successful update of all items, it clears the selected items list in the UI state,
     * toggles the category change dialog, and refreshes the data by calling `loadData()`.
     *
     * In case of an error during the update process, it logs the error, updates the submission state to reflect the error,
     * and potentially handles the error using [handleRepositoryResult] with a user-friendly error message.
     *
     * @param items A list of Long representing the IDs of the checklist items to update.
     * @param category The new ItemCategory to assign to the selected items.
     */
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

    /**
     * Copies selected items from the current checklist to a new checklist.
     *
     * This function performs the following steps:
     * 1. Creates a new checklist using the provided [checklistInput].
     * 2. If checklist creation fails, updates the submission state to [SubmissionState.Error] and returns.
     * 3. Extracts the ID of the newly created checklist from the successful result.
     * 4. Creates a map of selected item IDs to their corresponding quantities (if checked, otherwise defaults to 0).
     * 5. Copies the items to the new checklist using [checklistItemRepository.copyChecklistItemsToChecklist].
     * 6. Handles the result of the copy operation:
     *    - On success, clears the selected items, toggles the copy dialog visibility, and reloads the data.
     *    - On failure, displays an error message.
     * 7. Catches any exceptions during the process, logs the error, and updates the submission state to [SubmissionState.Error].
     *
     * @param checklistInput The input data for creating the new checklist.
     */
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

    /**
     * Adds a new checklist item to the checklist.
     *
     * This function takes a `ChecklistItemFormInputs` object containing the data for the new item.
     * It then constructs a `ChecklistItemInput` object and attempts to add it to the checklist using the
     * `checklistItemRepository`.  Upon successful addition, it toggles a drawer (presumably a UI element).
     *
     * If an error occurs during the process, it updates the `SubmissionState` to reflect the error and logs
     * the exception.
     *
     * @param input The `ChecklistItemFormInputs` object containing the data for the new checklist item.  This includes
     *              `name`, `price`, `quantity`, and `category`.
     *
     * @throws Exception if an unexpected error occurs during the process, causing the submission state to be updated to `Error`.
     *
     * @see ChecklistItemFormInputs
     * @see ChecklistItemInput
     * @see SubmissionState
     */
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

    /**
     * Edits an existing checklist item.
     *
     * This function updates a checklist item in the repository with the provided information.
     * It handles loading and error states and toggles the drawer upon successful update.
     *
     * @param checklistItemId The ID of the checklist item to be edited.
     * @param input An object containing the updated information for the checklist item (name, price, quantity).
     *
     *  The category is derived from the input name.  measureType, measureValue and photoRef are currently hardcoded default values.
     *  Future enhancement could include allowing the user to edit these fields.
     *
     * @throws Exception if any error occurs during the update process, such as network issues or database errors.  These errors are caught and logged,
     * and the submission state is updated to reflect the error.
     */
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

    /**
     * Deletes a checklist item, or a checklist item and its associated grocery item, based on provided IDs.
     *
     * This function handles deleting data from the checklist. It determines whether to delete only a checklist item
     * or also its associated grocery item based on the `groceryItemId`. If `groceryItemId` is null or 0L, only the
     * checklist item is deleted. Otherwise, both the checklist item and the grocery item are deleted.  The function
     * utilizes a `SubmissionState` to track the progress and outcome of the deletion process, updating it to
     * `Loading` at the start and either maintaining it as `Success` upon successful deletion, or updating to `Error`
     * in case of failure.  It also handles exceptions during the deletion process, logging errors and updating the
     * `SubmissionState` accordingly.
     *
     * @param checklistItemId The ID of the checklist item to delete.
     * @param groceryItemId The ID of the grocery item associated with the checklist item. If null or 0L, only the
     *                       checklist item is deleted; otherwise, both are deleted.
     */
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

    /**
     * Handles the result of a repository operation, updating the submission state accordingly.
     *
     * This function processes a [Result] object, representing the outcome of a repository interaction
     * (e.g., a network request or database operation).  It handles success, error, and loading states,
     * updating the UI submission state and optionally executing a success callback.
     *
     * @param result The [Result] object from the repository operation.  This can be a [Result.Success],
     *        [Result.Error], or [Result.Loading].
     * @param errorMessage A descriptive error message to log and display if the result is an error.
     * @param onSuccess An optional callback function to be executed if the result is a success.
     *        This callback is typically used to perform actions like navigating to a different screen
     *        or updating UI elements beyond the submission state.
     */
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