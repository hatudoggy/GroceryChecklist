package com.example.grocerychecklist.data.dao.firestoreImpl

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

abstract class FBaseDAOImpl<T : Any>(
    private val collectionPath: String
) {
    protected val currentUser: FirebaseUser
        get() = Firebase.auth.currentUser
            ?: throw IllegalStateException("User is not authenticated")

    protected val db: CollectionReference
        get() = Firebase.firestore
            .collection(FirestoreCollections.USERS)
            .document(currentUser.uid)
            .collection(collectionPath)


    protected abstract fun toFirestoreModel(obj: T): Map<String, Any?>
    protected abstract fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): T
    protected abstract fun getId(obj: T): Long

}