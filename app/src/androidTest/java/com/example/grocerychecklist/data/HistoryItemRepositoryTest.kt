package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryItemRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class HistoryItemRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var checklistRepository: ChecklistRepository
    private lateinit var checklistDAO: ChecklistDAO
    private lateinit var checklistItemRepository: ChecklistItemRepository
    private lateinit var checklistItemDAO: ChecklistItemDAO
    private lateinit var itemRepository: ItemRepository
    private lateinit var itemDAO: ItemDAO
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyDAO: HistoryDAO
    private lateinit var historyItemRepository: HistoryItemRepository
    private lateinit var historyItemDAO: HistoryItemDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        checklistDAO = db.checklistDAO()
        checklistRepository = ChecklistRepository(checklistDAO)
        itemDAO = db.itemDAO()
        itemRepository = ItemRepository(checklistItemDAO, itemDAO)
        checklistItemDAO = db.checklistItemDAO()
        checklistItemRepository = ChecklistItemRepository(checklistItemDAO, itemDAO)
        historyDAO = db.historyDAO()
        historyRepository = HistoryRepository(historyDAO)
        historyItemDAO = db.historyItemDAO()
        historyItemRepository = HistoryItemRepository(historyItemDAO)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

//    @Test
//    fun addMultipleHistoryItemsTest() = runBlocking  {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val allHistoryItems1 = historyItemRepository.getHistoryItems(historyId1, ChecklistItemOrder.Order).take(1).first()
//        assert(allHistoryItems1.isNotEmpty())
//        assert(allHistoryItems1[1].name == checklistItemInput[1].name)
//        assert(allHistoryItems1[1].price == checklistItemInput[1].price)
//        assert(allHistoryItems1[1].quantity == checklistItemInput[1].quantity)
//        assert(allHistoryItems1[1].category == checklistItemInput[1].category)
//        assert(allHistoryItems1[1].measureType == checklistItemInput[1].measureType)
//        assert(allHistoryItems1[1].measureValue == checklistItemInput[1].measureValue)
//        assert(allHistoryItems1[1].photoRef == checklistItemInput[1].photoRef)
//
//        assert(allHistoryItems1[1].isChecked)
//        assert(allHistoryItems1[2].isChecked)
//
//        val allHistoryItems2 = historyItemRepository.getHistoryItems(historyId2, ChecklistItemOrder.Order).take(1).first()
//        assert(allHistoryItems2.isNotEmpty())
//        assert(!allHistoryItems2[1].isChecked)
//    }
//
//    @Test
//    fun getSingleHistoryItemTest() = runBlocking  {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val allHistoryItems = historyItemRepository.getHistoryItem(1)
//        assert(allHistoryItems.name == checklistItemInput[0].name)
//        assert(allHistoryItems.price == checklistItemInput[0].price)
//        assert(allHistoryItems.quantity == checklistItemInput[0].quantity)
//        assert(allHistoryItems.category == checklistItemInput[0].category)
//        assert(allHistoryItems.measureType == checklistItemInput[0].measureType)
//        assert(allHistoryItems.measureValue == checklistItemInput[0].measureValue)
//        assert(allHistoryItems.photoRef == checklistItemInput[0].photoRef)
//
//        assert(!allHistoryItems.isChecked)
//    }
//
//    @Test
//    fun getMultipleHistoryItemTest() = runBlocking  {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val allHistoryItems1 = historyItemRepository.getHistoryItems(historyId1, ChecklistItemOrder.Order).take(1).first()
//        assert(allHistoryItems1.isNotEmpty())
//        assert(allHistoryItems1.size == 3)
//
//        val allHistoryItems2 = historyItemRepository.getHistoryItems(historyId2, ChecklistItemOrder.Order).take(1).first()
//        assert(allHistoryItems2.isNotEmpty())
//        assert(allHistoryItems2.size == 2)
//    }
//
//    @Test
//    fun getMultipleHistoryItemsCheckedAndUncheckedTest() = runBlocking  {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val checkedHistoryItems1 = historyItemRepository.getHistoryItems(historyId1, ChecklistItemOrder.Order, true).take(1).first()
//        val uncheckedHistoryItems1 = historyItemRepository.getHistoryItems(historyId1, ChecklistItemOrder.Order, false).take(1).first()
//        assert(checkedHistoryItems1.size == 2)
//        assert(uncheckedHistoryItems1.size == 1)
//
//        val checkedHistoryItems2 = historyItemRepository.getHistoryItems(historyId2, ChecklistItemOrder.Order, true).take(1).first()
//        val uncheckedHistoryItems2 = historyItemRepository.getHistoryItems(historyId2, ChecklistItemOrder.Order, false).take(1).first()
//        assert(checkedHistoryItems2.isEmpty())
//        assert(uncheckedHistoryItems2.size == 2)
//    }
//
//    @Test
//    fun getTotalChecklistItemsTest() = runBlocking {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val totalHistoryItems1 = historyItemRepository.getTotalHistoryItems(historyId1)
//        val totalHistoryItems2 = historyItemRepository.getTotalHistoryItems(historyId2)
//
//        assert(totalHistoryItems1 == 4)
//        assert(totalHistoryItems2 == 1)
//    }
//
//    @Test
//    fun getTotalChecklistItemPriceTest() = runBlocking {
//        val checklistMockData = ChecklistInputTestMockData
//        val checklistItemMockData = ChecklistItemInputTestMockData
//        val checklistInput = checklistMockData.checklistList
//
//        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
//        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])
//
//        val allChecklists = checklistRepository.getChecklists().take(1).first()
//
//        val checklistItemInput = checklistItemMockData.checklistItemList
//
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
//        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
//        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])
//
//        val checklistItemsList1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
//        val checklistItemsList2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
//
//        val historyId1 = historyRepository.addHistory(allChecklists[0])
//        val historyId2 = historyRepository.addHistory(allChecklists[1])
//
//        val checkedItems1 = setOf<Long>(2, 3)
//
//        historyItemRepository.addHistoryItems(historyId1, checklistItemsList1, checkedItems1)
//        historyItemRepository.addHistoryItems(historyId2, checklistItemsList2, setOf())
//
//        val priceSum = historyItemRepository.getTotalHistoryItemPrice(historyId1)
//        val computedPrice =
//            checklistItemInput[0].price +
//            checklistItemInput[1].price +
//            checklistItemInput[2].price +
//            checklistItemInput[4].price
//        assert(priceSum == computedPrice)
//    }
}