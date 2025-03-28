package com.example.grocerychecklist.data.mapper

object FirestoreIdConverter {
    fun toLong(firestoreId: String): Long {
        return firestoreId.hashCode().toLong()
    }
}