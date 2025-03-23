package com.example.grocerychecklist.util
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DataSyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val auth = FirebaseAuth.getInstance()

    override fun doWork(): Result {
        if (!isUserLoggedIn()) {
            Log.d("DataSync", "User is not logged in. Skipping data sync.")
            return Result.success()
        }

        val dbFile = File(applicationContext.getDatabasePath("grocery-app-database").absolutePath)
        val exportFile = File(applicationContext.getExternalFilesDir(null), auth.currentUser?.uid + ".db")

        return try {
            FileInputStream(dbFile).use { input ->
                FileOutputStream(exportFile).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
            Log.d("DataSync", "Database exported to: ${exportFile.absolutePath}")

            uploadDatabaseToFirebase(exportFile)
            Result.success()
        } catch (e: IOException) {
            Log.e("DataSync", "Failed to export database", e)
            Result.failure()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return !auth.currentUser?.email.isNullOrBlank();
    }

    private fun uploadDatabaseToFirebase(file: File) {
        val storageRef = FirebaseStorage.getInstance().reference
        val dbRef = storageRef.child("databases/${file.name}")

        val uri = Uri.fromFile(file)
        val uploadTask = dbRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            Log.d("DataSync", "Database uploaded successfully: ${it.metadata?.path}")
        }.addOnFailureListener {
            Log.e("DataSync", "Failed to upload database", it)
        }
    }
}
