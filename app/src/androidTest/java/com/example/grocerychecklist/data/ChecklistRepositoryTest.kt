package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.repository.ChecklistRepository
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
class ChecklistRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var checklistRepository: ChecklistRepository
    private lateinit var checklistDAO: ChecklistDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        checklistDAO = db.checklistDAO()
        checklistRepository = ChecklistRepository(checklistDAO)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addChecklistTest() = runBlocking  {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        assert(allChecklists.isNotEmpty())
        assert(allChecklists[1].name == checklistInput[1].name)
        assert(allChecklists[1].description == checklistInput[1].description)
        assert(allChecklists[1].icon == checklistInput[1].icon)
        assert(allChecklists[1].iconColor == checklistInput[1].iconColor)
        assert(allChecklists[1].createdAt is LocalDateTime)
        assert(allChecklists[1].updatedAt is LocalDateTime)
    }

    @Test
    fun updateChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        val checklist = allChecklists[1]

        Thread.sleep(1_000)

        val updatedChecklistInput = mockData.updatedChecklist
        checklistRepository.updateChecklist(checklist.id, updatedChecklistInput)

        val updatedChecklist = checklistRepository.getChecklist(checklist.id)
        assert(updatedChecklist.name == updatedChecklistInput.name) {"Name"}
        assert(updatedChecklist.description == updatedChecklistInput.description) {"Description"}
        assert(updatedChecklist.icon == updatedChecklistInput.icon) {"Icon"}
        assert(updatedChecklist.iconColor == updatedChecklistInput.iconColor) {"IconColor"}
        assert(updatedChecklist.updatedAt != checklist.updatedAt) {"UpdatedAt"}
    }

    @Test
    fun updateChecklistLastOpenedAtTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        val checklist = allChecklists[1]

        Thread.sleep(1_000)

        checklistRepository.updateChecklistLastOpenedAt(checklist.id)

        val updatedChecklist = checklistRepository.getChecklist(checklist.id)
        assert(updatedChecklist.lastOpenedAt != checklist.lastOpenedAt)
    }

    @Test
    fun updateChecklistLastShoppedAtTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        val checklist = allChecklists[1]

        Thread.sleep(1_000)

        checklistRepository.updateChecklistLastShoppedAt(checklist.id)

        val updatedChecklist = checklistRepository.getChecklist(checklist.id)
        assert(updatedChecklist.lastShopAt != checklist.lastShopAt)
    }

    @Test
    fun removeChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        val checklist = allChecklists[1]

        checklistRepository.deleteChecklist(checklist)

        val remainingChecklists = checklistRepository.getChecklists().take(1).first()
        assert(!remainingChecklists.contains(checklist))

        checklistRepository.deleteChecklist(allChecklists[0])
        checklistRepository.deleteChecklist(allChecklists[2])

        val remainingEmptyChecklist = checklistRepository.getChecklists().take(1).first()
        assert(remainingEmptyChecklist.isEmpty())
    }

    @Test
    fun getSingleChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val checklist = checklistRepository.getChecklist(2)

        assert(checklist.name == checklistInput[1].name)
        assert(checklist.description == checklistInput[1].description)
        assert(checklist.icon == checklistInput[1].icon)
        assert(checklist.iconColor == checklistInput[1].iconColor)
    }

    @Test
    fun getMultipleChecklistTest() = runBlocking {
        val mockData = ChecklistInputTestMockData
        val checklistInput = mockData.checklistList

        checklistRepository.addChecklist(checklistInput[0])
        checklistRepository.addChecklist(checklistInput[1])
        checklistRepository.addChecklist(checklistInput[2])

        val allChecklists = checklistRepository.getChecklists().take(1).first()
        assert(allChecklists.isNotEmpty())
    }

}