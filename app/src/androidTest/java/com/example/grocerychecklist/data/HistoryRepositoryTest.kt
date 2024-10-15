package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.HistoryDAO
import com.example.grocerychecklist.data.repository.ChecklistRepository
import com.example.grocerychecklist.data.repository.HistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addHistoryTest() = runBlocking  {
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
        assert(allHistories[1].iconColor == checklistInput[1].iconColor)

        assert(allHistories[1].name == allChecklists[1].name)
        assert(allHistories[1].description == allChecklists[1].description)
        assert(allHistories[1].icon == allChecklists[1].icon)
        assert(allHistories[1].iconColor == allChecklists[1].iconColor)
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
        assert(history.iconColor == checklistInput[1].iconColor)

        assert(history.name == allChecklists[1].name)
        assert(history.description == allChecklists[1].description)
        assert(history.icon == allChecklists[1].icon)
        assert(history.iconColor == allChecklists[1].iconColor)
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