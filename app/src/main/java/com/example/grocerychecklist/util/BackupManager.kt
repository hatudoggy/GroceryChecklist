package com.example.grocerychecklist.util

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

class BackupManager(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastChangeTime = 0L
    private var uploadJob: Job? = null
    private val debounceTime: Long = 10000

//    fun triggerBackup() {
//        lastChangeTime = System.currentTimeMillis()
//
//        // Cancel any existing upload job
//        uploadJob?.cancel()
//
//        // Schedule a new upload job
//        uploadJob = scope.launch {
//            delay(debounceTime)
//            if (System.currentTimeMillis() - lastChangeTime >= debounceTime) {
//                Log.d("DataSync", "Uploading this bih!")
//                DatabaseUploader.uploadDatabase(context, "grocery-app-database")
//            }
//        }
//    }

    fun triggerBackup() {
        // Check if the user is logged in
        if (!AuthUtils.isUserLoggedIn()) {
            Log.d("DataSync", "User is not logged in. Skipping backup.")
            return
        }

        lastChangeTime = System.currentTimeMillis()

        // Schedule a backup after `debounceTime`
        val workRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .setInitialDelay(debounceTime, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "DatabaseBackup",
            ExistingWorkPolicy.REPLACE, // Replace any existing scheduled work
            workRequest
        )
    }
}
