package com.example.grocerychecklist.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class DataSyncWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        return if (DatabaseSyncUtils.uploadDatabase(applicationContext, "grocery-app-database")) {
            Result.success()
        } else {
            Log.e("DataSyncWorker", "Database upload failed.")
            Result.failure()
        }
    }
}
