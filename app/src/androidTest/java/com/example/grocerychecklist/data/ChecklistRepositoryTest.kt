package com.example.grocerychecklist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ChecklistRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var checklistRepository: ChecklistRepository
    private lateinit var checklistDAO: ChecklistDAO

    private var icon = IconOption.Android
    private var color = ColorOption.White

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        checklistDAO  = db.checklistDAO()
        checklistRepository = ChecklistRepository(checklistDAO)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addChecklistTest() = runBlocking  {
        // Arrange
        val checklistInput = ChecklistInput(
            name = "Test Checklist",
            description = "Test Description",
            icon = icon,
            iconColor = color
        )

        // Act
        checklistRepository.addChecklist(checklistInput)

        // Assert
        val allChecklists = checklistRepository.getChecklists().take(1).toList().first()
        assert(allChecklists.isNotEmpty())
        assert(allChecklists[0].name == "Test Checklist")
    }

    @Test
    fun updateChecklistTest() = runBlocking {
        // Arrange
        val checklistInput = ChecklistInput(
            name = "Test Checklist",
            description = "Test Description",
            icon = icon,
            iconColor = color
        )

        checklistRepository.addChecklist(checklistInput)

        val allChecklists = checklistRepository.getChecklists().take(1).toList().first()
        val checklist = allChecklists[0]

        val updatedChecklistInput = ChecklistInput(
            name = "Updated Checklist",
            description = "Updated Description",
            icon = icon,
            iconColor = color
        )

        // Act
        checklistRepository.updateChecklist(checklist.id, updatedChecklistInput)

        // Assert
        val updatedChecklist = checklistRepository.getChecklist(checklist.id)
        assert(updatedChecklist.name == "Updated Checklist")
    }

    @Test
    fun deleteChecklistTest() = runBlocking {
        // Arrange
        val checklistInput = ChecklistInput(
            name = "Test Checklist",
            description = "Test Description",
            icon = icon,
            iconColor = color
        )

        checklistRepository.addChecklist(checklistInput)

        val allChecklists = checklistRepository.getChecklists().take(1).toList().first()
        val checklist = allChecklists[0]

        // Act
        checklistRepository.deleteChecklist(checklist)

        // Assert
        val remainingChecklists = checklistRepository.getChecklists().take(1).toList().first()
        assert(remainingChecklists.isEmpty())
    }

}