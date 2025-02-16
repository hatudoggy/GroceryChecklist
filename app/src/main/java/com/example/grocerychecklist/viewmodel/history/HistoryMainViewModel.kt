package com.example.grocerychecklist.viewmodel.history

import ItemCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.mapper.ChecklistItemInput
import com.example.grocerychecklist.data.mapper.HistoryMapped
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class HistoryMainViewModel(
    private val navigator: Navigator, private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryMainState())
    val state: StateFlow<HistoryMainState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), HistoryMainState()
    )

    init {
        loadHistoryData()
    }

    fun onEvent(event: HistoryMainEvent) {
        when (event) {
            is HistoryMainEvent.ToggleCard -> {
                // _state.value.cardStates[event.id] = !_state.value.cardStates[event.id]!!
                _state.update {
                    it.copy(cardStates = it.cardStates.toMutableMap().apply {
                        this[event.id] = this[event.id] != true
                    })
                }
            }

            is HistoryMainEvent.NavigateHistory -> {
                navigator.navigate(Routes.HistoryDetail (event.historyId))
            }

            is HistoryMainEvent.AddMockData -> {
                viewModelScope.launch {
                    addMockData()
                }
            }
        }
    }

    fun sortHistoryData(historyData: List<HistoryMapped>): List<HistoryMapped> {
        val sortedHistoryData = historyData.sortedByDescending {
            when (val createdAt = it.history.createdAt) {
                else -> createdAt.toLocalDate()
            }
        }

        sortedHistoryData.forEach { data ->
            val month = data.history.createdAt.format(DateTimeFormatter.ofPattern("MMM yyyy"))

            if (!_state.value.monthsList.contains(month)) {
                _state.value.monthsList.add(month)
            }
        }

        return sortedHistoryData
    }

    fun loadHistoryData() {
        viewModelScope.launch {
            val unsortedCards =
                historyRepository.getAggregatedHistory().stateIn(viewModelScope).value
            _state.value = _state.value.copy(
                // Sort the cards by date in descending order
                cards = sortHistoryData(unsortedCards)
            )
        }
    }


    suspend fun addMockData() {
        val checklistRepository = appModule.checklistRepository
        val checklistItemRepository = appModule.checklistItemRepository
        val historyRepository = appModule.historyRepository
        val historyItemRepository = appModule.historyItemRepository

        val checklistMockData = ChecklistInputTestMockData.checklistList
        val checklistItemMockData = ChecklistItemInputTestMockData.checklistItemList

        // Create checklists
        val checklistId1 = checklistRepository.addChecklist(checklistMockData[0])
        val checklistId2 = checklistRepository.addChecklist(checklistMockData[1])
        val checklistId3 = checklistRepository.addChecklist(checklistMockData[2])
        val checklistId4 = checklistRepository.addChecklist(checklistMockData[3])

        val allChecklists = checklistRepository.getChecklists().stateIn(viewModelScope).value

        // Add checklist items to first checklist with custom categories
        checklistItemRepository.addChecklistItem(
            checklistId1, checklistItemMockData[0]
        )
        checklistItemRepository.addChecklistItem(
            checklistId1, checklistItemMockData[1]
        )
        checklistItemRepository.addChecklistItem(
            checklistId1, checklistItemMockData[2]
        )
        checklistItemRepository.addChecklistItem(
            checklistId1, checklistItemMockData[3]
        )
        checklistItemRepository.addChecklistItem(
            checklistId3, checklistItemMockData[2]
        )
        checklistItemRepository.addChecklistItem(
            checklistId4, checklistItemMockData[3]
        )


        val checklistItemsList1 =
            checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order)
                .take(1).first()

        val historyId1 = historyRepository.addHistory(allChecklists[0])

        // Add history items
        historyItemRepository.addHistoryItems(
            historyId1, checklistItemsList1, checkedItems = setOf(1, 2, 3, 4)
        )

        // Add checklist items to second checklist with custom categories
        checklistItemRepository.addChecklistItem(
            checklistId3, checklistItemMockData[3]
        )
        checklistItemRepository.addChecklistItem(
            checklistId4, checklistItemMockData[2]
        )
        checklistItemRepository.addChecklistItem(
            checklistId2, checklistItemMockData[4]
        )

        val checklistItemsList2 =
            checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order)
                .stateIn(viewModelScope).value

        val historyId2 = historyRepository.addHistory(allChecklists[2])

        // Add history items
        historyItemRepository.addHistoryItems(
            historyId2, checklistItemsList2, checkedItems = setOf(1)
        )
    }


    object ChecklistInputTestMockData {

        val checklist1 = ChecklistInput(
            name = "Test Checklist",
            description = "This is a test Feb Checklist",
            icon = IconOption.MAIN_GROCERY,
            iconBackgroundColor = ColorOption.CopySkyGreen
        )

        val checklist2 = ChecklistInput(
            name = "Jan Checklist",
            description = "A test checklist 2",
            icon = IconOption.PET_SUPPLIES,
            iconBackgroundColor = ColorOption.CopyFurryBrown
        )

        val checklist3 = ChecklistInput(
            name = "Feb Checklist",
            description = "A test checklist 3",
            icon = IconOption.PERSONAL_CARE,
            iconBackgroundColor = ColorOption.CopySoftPurple
        )

        val updatedChecklist = ChecklistInput(
            name = "Checklist Updated",
            description = "Hep hep Horay!",
            icon = IconOption.SWEETS,
            iconBackgroundColor = ColorOption.CopyCherryPink
        )

        val checklistList = listOf(
            checklist1, checklist2, checklist3, updatedChecklist
        )

    }

    object ChecklistItemInputTestMockData {

        val checklistItem1 = ChecklistItemInput(
            name = "Checklist Item 1",
            price = 20.00,
            quantity = 4,
            category = "Poultry",
            measureType = "kg",
            measureValue = 20.0,
            photoRef = ""
        )

        val checklistItem2 = ChecklistItemInput(
            name = "Checklist Item 2",
            price = 85.00,
            quantity = 5,
            category = "Meat",
            measureType = "kg",
            measureValue = 20.0,
            photoRef = ""
        )

        val checklistItem3 = ChecklistItemInput(
            name = "Checklist Item 3",
            price = 40.00,
            quantity = 6,
            category = "Dairy",
            measureType = "kg",
            measureValue = 20.0,
            photoRef = ""
        )

        val checklistItem4 = ChecklistItemInput(
            name = "Checklist Item 4",
            price = 10.00,
            quantity = 7,
            category = "Cleaning",
            measureType = "kg",
            measureValue = 20.0,
            photoRef = ""
        )

        val checklistItem5 = ChecklistItemInput(
            name = "Checklist Item 5",
            price = 60.00,
            quantity = 8,
            category = "Cosmetic",
            measureType = "kg",
            measureValue = 20.0,
            photoRef = ""
        )

        val updatedChecklistItem = ChecklistItemInput(
            name = "Walter White Blue Stone",
            price = 69.00,
            quantity = 7,
            category = "Medicine",
            measureType = "g",
            measureValue = 69.0,
            photoRef = "stone.jpg"
        )

        val checklistItemList = listOf(
            checklistItem1,
            checklistItem2,
            checklistItem3,
            checklistItem4,
            checklistItem5,
        )
    }
}