package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.BaseDAO
import com.example.grocerychecklist.data.mapper.FirestoreIdConverter
import com.example.grocerychecklist.util.IdGenerator
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

abstract class FBaseIUDDAOImpl<T : Any>(
    private val collectionPath: String,
) : BaseDAO<T>  {
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

    override suspend fun insert(obj: T): Long {
        val newId = IdGenerator.nextID()
        val firestoreModel = toFirestoreModel(obj)
        db.document(newId.toString()).set(firestoreModel).await()
        return newId
    }

    override suspend fun update(obj: T) {
        val id = getId(obj)
        val firestoreModel = toFirestoreModel(obj)
        db.document(id.toString()).set(firestoreModel).await()
    }

    override suspend fun delete(vararg obj: T) {
        obj.forEach { item ->
            val id = getId(item)
            db.document(id.toString()).delete().await()
        }
    }
}