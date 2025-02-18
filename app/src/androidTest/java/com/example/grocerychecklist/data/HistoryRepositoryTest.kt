package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.model.History
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime

class HistoryRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var checklistRepository: ChecklistRepository
    private lateinit var checklistDAO: ChecklistDAO
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyDAO: HistoryDAO
    private lateinit var checklistItemRepository: ChecklistItemRepository
    private lateinit var historyItemRepository: HistoryItemRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        checklistDAO = db.checklistDAO()
        checklistRepository = ChecklistRepository(checklistDAO)
        historyDAO = db.historyDAO()
        historyRepository = HistoryRepository(historyDAO)
        checklistItemRepository = ChecklistItemRepository(db.checklistItemDAO(), db.itemDAO())
        historyItemRepository = HistoryItemRepository(db.historyItemDAO())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getHistoryItemAggregatedTest() = runTest {
        // Setup checklist and items
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val allChecklists = checklistRepository.getChecklists().take(1).first()

        val checklistItemInput = checklistItemMockData.checklistItemList

        // First Checklist/History Item
        // Add checklist items with modified categories for aggregation
        checklistItemRepository.addChecklistItem(
            checklistId1,
            checklistItemInput[0].copy(category = "Fresh")
        )
        checklistItemRepository.addChecklistItem(
            checklistId1,
            checklistItemInput[1].copy(category = "Frozen")
        )
        checklistItemRepository.addChecklistItem(
            checklistId1,
            checklistItemInput[2].copy(category = "Frozen")
        )

        val checklistItemsList1 = checklistItemRepository.getChecklistItems(
            checklistId1,
            ChecklistItemOrder.Order
        ).take(1).first()

        val historyId1 = historyRepository.addHistory(allChecklists[0])

        // Add item to history (checked status irrelevant for aggregation)
        historyItemRepository.addHistoryItems(
            historyId1,
            checklistItemsList1,
            checkedItems = setOf(1, 2, 3) // Assume these are valid positions
        )

        // Second Checklist/History Item
        // Add checklist items with modified categories for aggregation
        checklistItemRepository.addChecklistItem(
            checklistId2,
            checklistItemInput[3].copy(category = "Medicine")
        )
        checklistItemRepository.addChecklistItem(
            checklistId2,
            checklistItemInput[2].copy(category = "Food")
        )
        checklistItemRepository.addChecklistItem(
            checklistId2,
            checklistItemInput[4].copy(category = "Medicine")
        )

        val checklistItemsList2 = checklistItemRepository.getChecklistItems(
            checklistId2,
            ChecklistItemOrder.Order
        ).take(1).first()

        val historyId2 = historyRepository.addHistory(allChecklists[1])

        // Add ALL items to history (checked status irrelevant for aggregation)
        historyItemRepository.addHistoryItems(
            historyId2,
            checklistItemsList2,
            checkedItems = setOf(1, 2, 3) // Assume these are valid positions
        )

        // Fetch aggregated data
        val aggregatedItems = historyDAO.getHistoryItemAggregated(historyId1).first()

        // Verify aggregation by category
        assert(aggregatedItems.size == 2) // Fresh and Frozen

        val freshCategory = aggregatedItems.find { it.category == "Fresh" }
        val frozenCategory = aggregatedItems.find { it.category == "Frozen" }

        // Check Fresh category aggregation
        assert(freshCategory != null)
        assert(freshCategory?.totalItems == 1)
        val expectedFreshSum = checklistItemInput[0].price * checklistItemInput[0].quantity
        assert(freshCategory?.sumOfPrice == expectedFreshSum)

        // Check Frozen category aggregation
        assert(frozenCategory != null)
        assert(frozenCategory?.totalItems == 2)
        val expectedFrozenSum =
            (checklistItemInput[1].price * checklistItemInput[1].quantity) +
                    (checklistItemInput[2].price * checklistItemInput[2].quantity)
        assert(frozenCategory?.sumOfPrice == expectedFrozenSum)

        // Verify total sum matches expectation
        val totalSum = aggregatedItems.sumOf { it.sumOfPrice }
        assert(totalSum == expectedFreshSum + expectedFrozenSum)

        // Verify HistoryWithAggregatedItems reflects the correct totalPrice
        val historyWithAggregated = historyDAO.getHistoryWithAggregatedItems().first()
        assert(historyWithAggregated.isNotEmpty())  // Ensure there's data

        // Flatten aggregated items from all history entries
        val allAggregatedItems = historyWithAggregated.flatMap { it.aggregatedItems }

        // Ensure each expected item is present in the aggregated list
        aggregatedItems.forEach { expectedItem ->
            assert(allAggregatedItems.any { it == expectedItem }) {
                "Expected item $expectedItem not found in actual results: $allAggregatedItems"
            }
        }
    }

    @Test
    fun addHistoryTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()

        historyRepository.addHistory(allChecklists[0])
        historyRepository.addHistory(allChecklists[1])
        historyRepository.addHistory(allChecklists[2])

        val allHistories = historyRepository.getHistories().take(1).first()

        assert(allHistories.isNotEmpty())
        assert(allHistories[1].name == checklistInput[1].name)
        assert(allHistories[1].description == checklistInput[1].description)
        assert(allHistories[1].icon == checklistInput[1].icon)
        assert(allHistories[1].iconColor == checklistInput[1].iconBackgroundColor)

        assert(allHistories[1].name == allChecklists[1].name)
        assert(allHistories[1].description == allChecklists[1].description)
        assert(allHistories[1].icon == allChecklists[1].icon)
        assert(allHistories[1].iconColor == allChecklists[1].iconBackgroundColor)
    }

    @Test
    fun getSingleChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()

        historyRepository.addHistory(allChecklists[0])
        historyRepository.addHistory(allChecklists[1])
        historyRepository.addHistory(allChecklists[2])

        val history = historyRepository.getHistory(2)

        assert(history.name == checklistInput[1].name)
        assert(history.description == checklistInput[1].description)
        assert(history.icon == checklistInput[1].icon)
        assert(history.iconColor == checklistInput[1].iconBackgroundColor)

        assert(history.name == allChecklists[1].name)
        assert(history.description == allChecklists[1].description)
        assert(history.icon == allChecklists[1].icon)
        assert(history.iconColor == allChecklists[1].iconBackgroundColor)
    }

    @Test
    fun getMultipleChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()

        historyRepository.addHistory(allChecklists[0])
        historyRepository.addHistory(allChecklists[1])
        historyRepository.addHistory(allChecklists[2])

        val allHistories = historyRepository.getHistories().take(1).first()
        assert(allHistories.isNotEmpty())
    }

}