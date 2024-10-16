package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.repository.ItemOrder
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
class ItemRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var itemRepository: ItemRepository
    private lateinit var itemDAO: ItemDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        itemDAO = db.itemDAO()
        itemRepository = ItemRepository(itemDAO)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addItemTest() = runBlocking  {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[2])
        itemRepository.addItem(itemInput[3])
        itemRepository.addItem(itemInput[4])

        val allItems = itemRepository.getItems(ItemOrder.Name).take(1).first()
        assert(allItems.isNotEmpty())
        assert(allItems.size == 5)
        assert(allItems[1].name == itemInput[1].name)
        assert(allItems[1].price == itemInput[1].price)
        assert(allItems[1].category == itemInput[1].category)
        assert(allItems[1].measureType == itemInput[1].measureType)
        assert(allItems[1].measureValue == itemInput[1].measureValue)
        assert(allItems[1].photoRef == itemInput[1].photoRef)
        assert(allItems[1].createdAt is LocalDateTime)
    }

    @Test
    fun updateChecklistItemTest() = runBlocking {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[2])
        itemRepository.addItem(itemInput[3])
        itemRepository.addItem(itemInput[4])

        val allItems = itemRepository.getItems(ItemOrder.Name).take(1).first()
        val item = allItems[1]

        Thread.sleep(1_000)

        val updatedItemInput = itemMockData.updatedItem
        itemRepository.updateItem(item.id, updatedItemInput)

        val updatedItem = itemRepository.getItem(item.id)
        assert(updatedItem.name == updatedItemInput.name)
        assert(updatedItem.price == updatedItemInput.price)
        assert(updatedItem.category == updatedItemInput.category)
        assert(updatedItem.measureType == updatedItemInput.measureType)
        assert(updatedItem.measureValue == updatedItemInput.measureValue)
        assert(updatedItem.photoRef == updatedItemInput.photoRef)
        assert(updatedItem.createdAt is LocalDateTime)
        assert(updatedItem.updatedAt != item.updatedAt)
    }

    @Test
    fun removeChecklistItemTest() = runBlocking {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[2])

        val allItems = itemRepository.getItems(ItemOrder.Name).take(1).first()
        val item = allItems[1]

        itemRepository.deleteItem(item)

        val remainingChecklistItems = itemRepository.getItems(ItemOrder.CreatedAt).take(1).first()
        assert(!remainingChecklistItems.contains(item))

        itemRepository.deleteItem(allItems[0])
        itemRepository.deleteItem(allItems[2])

        val remainingEmptyChecklist = itemRepository.getItems(ItemOrder.CreatedAt).take(1).first()
        assert(remainingEmptyChecklist.isEmpty())

    }

    @Test
    fun getSingleChecklistItemTest() = runBlocking {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        val itemId = itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[2])

        val allItems = itemRepository.getItems(ItemOrder.CreatedAt).take(1).first()
        val item = itemRepository.getItem(itemId)

        assert(item.name == itemInput[1].name) {"Name"}
        assert(item.price == itemInput[1].price) {"Price"}
        assert(item.category == itemInput[1].category) {"Category"}
        assert(item.measureType == itemInput[1].measureType) {"MeasureType"}
        assert(item.measureValue == itemInput[1].measureValue) {"MeasureValue"}
        assert(item.photoRef == itemInput[1].photoRef) {"PhotoRef"}
        assert(item.createdAt is LocalDateTime) {"CreatedAt"}

        assert(item == allItems[1]) {"Assert: Item"}
    }

    @Test
    fun getMultipleChecklistItemTest() = runBlocking {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[2])

        val allItems = itemRepository.getItems(ItemOrder.CreatedAt).take(1).first()

        assert(allItems.size == 3)
    }

    @Test
    fun searchChecklistItemByNameTest() = runBlocking {
        val itemMockData = ItemInputTestMockData
        val itemInput = itemMockData.itemList

        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[1])
        itemRepository.addItem(itemInput[0])
        itemRepository.addItem(itemInput[3])

        val search1 = itemRepository.searchItems("Item 1").take(1).first()
        val search2 = itemRepository.searchItems("Checklist 5").take(1).first()

        assert(search1.size == 2) {"Assert 1: The search size is either empty or not right but should be equal to 2"}
        assert(search2.isEmpty()) {"Assert 2: The search is should be empty"}
    }
}