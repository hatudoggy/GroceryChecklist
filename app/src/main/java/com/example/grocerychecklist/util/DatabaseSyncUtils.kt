package com.example.grocerychecklist.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import com.example.grocerychecklist.data.AppDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object DatabaseSyncUtils {
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun isUploadInProgress(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences("BackupPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("isUploading", false)
    }

    private fun setUploadInProgress(context: Context, inProgress: Boolean) {
        _isUploading.value = inProgress
        val sharedPrefs = context.getSharedPreferences("BackupPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit() { putBoolean("isUploading", inProgress) }
    }

    fun uploadDatabase(context: Context, dbName: String): Boolean {
        setUploadInProgress(context, true)

        if (!AuthUtils.isUserLoggedIn()) {
            Log.d("DataSync", "User is not logged in. Skipping upload.")
            _isUploading.value = false
            return false
        }

        val dbFile = File(context.getDatabasePath(dbName).absolutePath)
        val exportFile = File(context.getExternalFilesDir(null), AuthUtils.getAuth().currentUser!!.uid + ".db")

        return try {
            FileInputStream(dbFile).use { input ->
                FileOutputStream(exportFile).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
            Log.d("DataSync", "Database exported: ${exportFile.absolutePath}")

            uploadToFirebase(context, exportFile)
            true
        } catch (e: IOException) {
            Log.e("DataSync", "Failed to export database", e)
            setUploadInProgress(context, false)
            false
        }
    }

    private fun uploadToFirebase(context: Context, file: File) {
        val storageRef = FirebaseStorage.getInstance().reference
        val dbRef = storageRef.child("databases/${file.name}")

        val uri = Uri.fromFile(file)
        val uploadTask = dbRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            Log.d("DataSync", "Database uploaded successfully: ${it.metadata?.path}")
            setUploadInProgress(context, false)
        }.addOnFailureListener {
            Log.e("DataSync", "Failed to upload database", it)
            setUploadInProgress(context, false)
        }
    }

    fun downloadDatabase(context: Context, dbName: String) {
        if (!AuthUtils.isUserLoggedIn()) {
            Log.d("DataSync", "User is not logged in. Skipping download.")
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val dbRef = storageRef.child("databases/${AuthUtils.getAuth().currentUser!!.uid}.db")

        val localFile = File(context.getDatabasePath(dbName).absolutePath)

        dbRef.getFile(localFile).addOnSuccessListener {
            Log.d("DataSync", "Database downloaded successfully: ${localFile.absolutePath}")

            // Ensure Room reloads the new database
            restartRoomDatabase(context)
        }.addOnFailureListener {
            Log.e("DataSync", "Failed to download database", it)
        }
    }

    private fun restartRoomDatabase(context: Context) {
        try {
            AppDatabase.getDatabase(context).close() // Close the database
            AppDatabase.resetInstance() // Force Room to reinitialize

            Log.d("DataSync", "Room database closed and will reload with new data.")

            // Recreate an instance immediately if needed
            AppDatabase.getDatabase(context)
        } catch (e: Exception) {
            Log.e("DataSync", "Failed to restart Room database", e)
        }
    }

}