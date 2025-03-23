package com.example.grocerychecklist.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class oldDataSync {
    fun exportAndUploadDatabase(context: Context) {
        val dbFile = File(context.getDatabasePath("grocery-app-database").absolutePath)
        val exportFile = File(context.getExternalFilesDir(null), "exported_db.db")

        try {
            FileInputStream(dbFile).use { input ->
                FileOutputStream(exportFile).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
            Log.d("DataSync", "Database exported to: ${exportFile.absolutePath}")

            // After exporting, upload to Firebase
            uploadDatabaseToFirebase(exportFile)

        } catch (e: IOException) {
            Log.e("DataSync", "Failed to export database", e)
        }
    }

    fun uploadDatabaseToFirebase(file: File) {
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