package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.example.grocerychecklist.data.repository.ChecklistItemRepository
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.ItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ChecklistItemRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var checklistRepository: ChecklistRepository
    private lateinit var checklistDAO: ChecklistDAO
    private lateinit var checklistItemRepository: ChecklistItemRepository
    private lateinit var checklistItemDAO: ChecklistItemDAO
    private lateinit var itemRepository: ItemRepository
    private lateinit var itemDAO: ItemDAO

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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addChecklistItemTest() = runBlocking  {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])

        val allChecklistItems = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        assert(allChecklistItems.isNotEmpty())
        assert(allChecklistItems[1].item.name == checklistItemInput[1].name)
        assert(allChecklistItems[1].item.price == checklistItemInput[1].price)
        assert(allChecklistItems[1].checklistItem.quantity == checklistItemInput[1].quantity)
        assert(allChecklistItems[1].item.category == checklistItemInput[1].category)
        assert(allChecklistItems[1].item.measureType == checklistItemInput[1].measureType)
        assert(allChecklistItems[1].item.measureValue == checklistItemInput[1].measureValue)
        assert(allChecklistItems[1].item.photoRef == checklistItemInput[1].photoRef)
        assert(allChecklistItems[1].item.createdAt is LocalDateTime)

        val allChecklistItems2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
        assert(allChecklistItems2.isNotEmpty())
        assert(allChecklistItems2.size == 2)
    }

    @Test
    fun updateChecklistItemTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])

        val allChecklistItems = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        val checklistItem = allChecklistItems[1]

        Thread.sleep(1_000)

        val updatedChecklistItemInput = checklistItemMockData.updatedChecklistItem
        checklistItemRepository.updateChecklistItem(checklistItem.checklistItem.id, updatedChecklistItemInput)

        val updatedChecklistItem = checklistItemRepository.getChecklistItem(checklistItem.checklistItem.id)
        assert(updatedChecklistItem.item.name == updatedChecklistItemInput.name)
        assert(updatedChecklistItem.item.price == updatedChecklistItemInput.price)
        assert(updatedChecklistItem.checklistItem.quantity == updatedChecklistItemInput.quantity)
        assert(updatedChecklistItem.item.category == updatedChecklistItemInput.category)
        assert(updatedChecklistItem.item.measureType == updatedChecklistItemInput.measureType)
        assert(updatedChecklistItem.item.measureValue == updatedChecklistItemInput.measureValue)
        assert(updatedChecklistItem.item.photoRef == updatedChecklistItemInput.photoRef)
        assert(updatedChecklistItem.item.createdAt is LocalDateTime)
        assert(updatedChecklistItem.item.updatedAt != checklistItem.item.updatedAt)
        assert(updatedChecklistItem.checklistItem.updatedAt != checklistItem.checklistItem.updatedAt)
    }

    @Test
    fun changeChecklistItemOrderTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId = checklistRepository.addChecklist(checklistInput[0])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[4])

        val allChecklistItems = checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Order).take(1).first()
        val checklistItem = allChecklistItems[3]

        checklistItemRepository.changeChecklistOrder(checklistId, checklistItem.checklistItem.id, 2)
        val reorderedChecklistItems = checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Order).take(1).first()
        val orderedChecklistItem = reorderedChecklistItems[1]

        assert(checklistItem.checklistItem.id == orderedChecklistItem.checklistItem.id) {"The previous order of the specified item is not equal to the new order"}
        assert(allChecklistItems[0].checklistItem.order == reorderedChecklistItems[0].checklistItem.order) {"Items 1 order is not equal"}
        assert(allChecklistItems[1].checklistItem.order == reorderedChecklistItems[1].checklistItem.order) {"Items 2 order is not equal"}
        assert(allChecklistItems[2].checklistItem.order == reorderedChecklistItems[2].checklistItem.order) {"Items 3 order is not equal"}
        assert(allChecklistItems[3].checklistItem.order == reorderedChecklistItems[3].checklistItem.order) {"Items 4 order is not equal"}
        assert(allChecklistItems[4].checklistItem.order == reorderedChecklistItems[4].checklistItem.order) {"Items 5 order is not equal"}
    }

    @Test
    fun removeChecklistItemTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])

        val allChecklistItems1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        val checklistItem = allChecklistItems1[1]

        checklistItemRepository.deleteChecklistItem(checklistItem.checklistItem)

        val remainingChecklistItems = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        assert(!remainingChecklistItems.contains(checklistItem))
        assert(remainingChecklistItems[0].checklistItem.order == 1)
        assert(remainingChecklistItems[1].checklistItem.order == 2)

        checklistItemRepository.deleteChecklistItem(allChecklistItems1[0].checklistItem)
        checklistItemRepository.deleteChecklistItem(allChecklistItems1[2].checklistItem)

        val remainingEmptyChecklist = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        assert(remainingEmptyChecklist.isEmpty())

        val allChecklistItems2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
        assert(allChecklistItems2.isNotEmpty())
    }

    @Test
    fun getSingleChecklistItemTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
        val checklistItemId1 = checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        val checklistItemId2 = checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])

        val allChecklistItems1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        val allChecklistItems2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()
        val checklistItem1 = checklistItemRepository.getChecklistItem(checklistItemId1)
        val checklistItem2 = checklistItemRepository.getChecklistItem(checklistItemId2)

        assert(checklistItem1.item.name == checklistItemInput[1].name) {"Name"}
        assert(checklistItem1.item.price == checklistItemInput[1].price) {"Price"}
        assert(checklistItem1.checklistItem.quantity == checklistItemInput[1].quantity) {"Quantity"}
        assert(checklistItem1.item.category == checklistItemInput[1].category) {"Category"}
        assert(checklistItem1.item.measureType == checklistItemInput[1].measureType) {"MeasureType"}
        assert(checklistItem1.item.measureValue == checklistItemInput[1].measureValue) {"MeasureValue"}
        assert(checklistItem1.item.photoRef == checklistItemInput[1].photoRef) {"PhotoRef"}
        assert(checklistItem1.item.createdAt is LocalDateTime) {"CreatedAt"}

        assert(checklistItem1 == allChecklistItems2[0]) {"Assert: Checklist Item 1"}
        assert(checklistItem2 == allChecklistItems1[2]) {"Assert: Checklist Item 2"}
    }

    @Test
    fun getMultipleChecklistItemTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])

        val allChecklistItems1 = checklistItemRepository.getChecklistItems(checklistId1, ChecklistItemOrder.Order).take(1).first()
        val allChecklistItems2 = checklistItemRepository.getChecklistItems(checklistId2, ChecklistItemOrder.Order).take(1).first()

        assert(allChecklistItems1.size == 3)
        assert(allChecklistItems2.size == 2)
    }

    @Test
    fun searchChecklistItemByNameTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[4])

        val checklistItem1 = checklistItemRepository.searchChecklistItems(checklistId1,"Checklist Item 5").take(1).first()
        val checklistItem2 = checklistItemRepository.searchChecklistItems(checklistId2,"Checklist Item 5").take(1).first()

        assert(checklistItem1.size == 2) {"Assert 1: The search is either empty or not right"}
        assert(checklistItem2.isEmpty()) {"Assert 2: The search is should be empty"}
    }

    @Test
    fun getTotalChecklistItemsTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId1 = checklistRepository.addChecklist(checklistInput[0])
        val checklistId2 = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId1, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId2, checklistItemInput[4])

        val totalChecklistItems1 = checklistItemRepository.getTotalChecklistItems(checklistId1)
        val totalChecklistItems2 = checklistItemRepository.getTotalChecklistItems(checklistId2)

        assert(totalChecklistItems1 == 2)
        assert(totalChecklistItems2 == 3)
    }

    @Test
    fun getTotalChecklistItemPriceTest() = runBlocking {
        val checklistMockData = ChecklistInputTestMockData
        val checklistItemMockData = ChecklistItemInputTestMockData
        val checklistInput = checklistMockData.checklistList

        val checklistId = checklistRepository.addChecklist(checklistInput[0])
        val checklistIdExtra = checklistRepository.addChecklist(checklistInput[1])

        val checklistItemInput = checklistItemMockData.checklistItemList

        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[0])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[1])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[2])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[3])
        checklistItemRepository.addChecklistItem(checklistId, checklistItemInput[4])

        val priceSum1 = checklistItemRepository.getTotalChecklistItemPrice(checklistId)
        var computedPrice =
            checklistItemInput[0].price +
            checklistItemInput[1].price +
            checklistItemInput[2].price +
            checklistItemInput[3].price +
            checklistItemInput[4].price
        assert(priceSum1 == computedPrice)

        checklistItemRepository.addChecklistItem(checklistId, checklistItemMockData.updatedChecklistItem)
        val priceSum2 = checklistItemRepository.getTotalChecklistItemPrice(checklistId)
        computedPrice =
            checklistItemInput[0].price +
            checklistItemInput[1].price +
            checklistItemInput[2].price +
            checklistItemInput[3].price +
            checklistItemInput[4].price +
            checklistItemMockData.updatedChecklistItem.price
        assert(priceSum2 == computedPrice)

        val allChecklistItems = checklistItemRepository.getChecklistItems(checklistId, ChecklistItemOrder.Order).take(1).first()
        checklistItemRepository.deleteChecklistItem(allChecklistItems[3].checklistItem)
        checklistItemRepository.deleteChecklistItem(allChecklistItems[4].checklistItem)
        val priceSum3 = checklistItemRepository.getTotalChecklistItemPrice(checklistId)
        computedPrice =
            checklistItemInput[0].price +
            checklistItemInput[1].price +
            checklistItemInput[2].price +
            checklistItemMockData.updatedChecklistItem.price
        assert(priceSum3 == computedPrice)

        checklistItemRepository.addChecklistItem(checklistIdExtra, checklistItemInput[2])
        val priceSumExtra = checklistItemRepository.getTotalChecklistItemPrice(checklistIdExtra)
        assert(priceSum3 == computedPrice)
        assert(priceSumExtra == checklistItemInput[2].price)
    }
}