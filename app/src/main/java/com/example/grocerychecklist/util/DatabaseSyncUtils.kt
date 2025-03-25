package com.example.grocerychecklist.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.data.AppDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

enum class DataState {
    IDLE,            // Default state, nothing happening
    FETCHING,        // Fetching data
    SAVING_UNSYNCED  // Saving unsynced data
}

object DatabaseSyncUtils {
    private val _dataState = MutableStateFlow(DataState.IDLE)
    val dataState: StateFlow<DataState> = _dataState

    suspend fun fetchData() {
        _dataState.value = DataState.FETCHING
        try {
            val itemsFetchResult =
                runCatching { appModule.itemRepository.fetchItemsFromFirestoreAndInsertToRoom() }
            val checklistFetchResult =
                runCatching { appModule.checklistRepository.fetchChecklistsFromFirestoreAndInsertToRoom() }
        } catch (e: Exception) {
            Log.e("DataSync", "Failed to fetch data", e)
        } finally {
            _dataState.value = DataState.IDLE
        }
    }

    suspend fun saveUnsyncedData() {
        _dataState.value = DataState.SAVING_UNSYNCED

        try {
            val itemsSaveUnsyncedResult =
                runCatching { appModule.itemRepository.saveUnsyncedItems() }
            val checklistSaveUnsyncedResult =
                runCatching { appModule.checklistRepository.saveUnsyncedChecklists() }
        } catch (e: Exception) {
            Log.e("DataSync", "Failed to save unsynced data", e)
        } finally {
            _dataState.value = DataState.IDLE
        }
    }

    private fun setUploadInProgress(context: Context, inProgress: Boolean) {
        _dataState.value = DataState.IDLE
        val sharedPrefs = context.getSharedPreferences("BackupPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit() { putBoolean("isFetchingData", inProgress) }
    }

    suspend fun uploadDatabase(context: Context, database: AppDatabase): String? =
        withContext(Dispatchers.IO) {
            setUploadInProgress(context, true)

            if (!AuthUtils.isUserLoggedIn()) {
                Log.d("DataSync", "User is not logged in. Skipping upload.")
                setUploadInProgress(context, false)
                return@withContext null
            }

            val databaseName = database.openHelper.databaseName
            val databasePath = context.getDatabasePath(databaseName).absolutePath
            val exportedFilePath = "${context.cacheDir}/$databaseName"
            val storagePath = "databases/${AuthUtils.getAuth().currentUser!!.uid}/$databaseName"

            try {
                // Close the database before copying.
                database.close()

                copyDatabase(databasePath, exportedFilePath)
                val uri = Uri.fromFile(File(exportedFilePath))
                val downloadUrl = uploadToFirebaseStorage(uri, storagePath)

                File(exportedFilePath).delete() // Clean up

                setUploadInProgress(context, false)
                downloadUrl
            } catch (e: IOException) {
                Log.e("DataSync", "Failed to export database", e)
                setUploadInProgress(context, false)
                null
            } catch (e: Exception) {
                Log.e("DataSync", "Firebase upload failed", e)
                setUploadInProgress(context, false)
                null
            } finally {
                // Reopen the database after the upload attempt.
                AppDatabase.getDatabase(context)
            }
        }

    private fun copyDatabase(databasePath: String, exportedFilePath: String) {
        FileInputStream(databasePath).use { inputStream ->
            FileOutputStream(exportedFilePath).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    }

    private suspend fun uploadToFirebaseStorage(fileUri: Uri, storagePath: String): String? {
        return try {
            val storageRef = Firebase.storage.reference.child(storagePath)
            val uploadTask = storageRef.putFile(fileUri).await()
            uploadTask.metadata?.reference?.downloadUrl?.await().toString()
        } catch (e: Exception) {
            Log.e("DataSync", "Firebase upload failed", e)
            null
        }
    }

    suspend fun downloadDatabase(context: Context, database: AppDatabase) =
        withContext(Dispatchers.IO) {
            if (!AuthUtils.isUserLoggedIn()) {
                Log.d("DataSync", "User is not logged in. Skipping download.")
                return@withContext
            }

            val databaseName = database.openHelper.databaseName
            val storagePath = "databases/${AuthUtils.getAuth().currentUser!!.uid}/$databaseName"
            val localFilePath = context.getDatabasePath(databaseName).absolutePath

            try {
                val storageRef = Firebase.storage.reference.child(storagePath)
                val fileUri = Uri.fromFile(File(localFilePath))
                storageRef.getFile(fileUri).await()

                Log.d("DataSync", "Database downloaded successfully: $localFilePath")

                // Ensure Room reloads the new database
                restartRoomDatabase(context, database)
            } catch (e: Exception) {
                Log.e("DataSync", "Failed to download database", e)
            }
        }

    private fun restartRoomDatabase(context: Context, database: AppDatabase) {
        try {
            database.close()
            AppDatabase.resetInstance()
            Log.d("DataSync", "Room database closed and will reload with new data.")
            AppDatabase.getDatabase(context) //recreate database instance
        } catch (e: Exception) {
            Log.e("DataSync", "Failed to restart Room database", e)
        }
    }
}