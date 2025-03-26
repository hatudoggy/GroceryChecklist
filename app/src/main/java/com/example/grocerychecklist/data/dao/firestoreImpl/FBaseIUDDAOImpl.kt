package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.BaseDAO
import com.example.grocerychecklist.data.mapper.FirestoreIdConverter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

abstract class FBaseIUDDAOImpl<T : Any>(
    private val collectionPath: String,
    private val idField: String = "id"
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
    protected abstract fun fromFirestoreModel(snapshot: DocumentSnapshot): T

    override suspend fun insert(obj: T): Long {
        val firestoreModel = toFirestoreModel(obj)
        val docRef = db.add(firestoreModel).await()
        val docId = docRef.id
        val hashedId = FirestoreIdConverter.toLong(docId)
        db.document(docId).update(idField, hashedId).await()
        return hashedId
    }

    override suspend fun update(obj: T) {
        val firestoreModel = toFirestoreModel(obj)
        val query = db.whereEqualTo(idField, firestoreModel[idField]).get().await()
        val doc = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Document with ${firestoreModel[idField]} not found.")

        doc.reference.set(firestoreModel).await()
    }

    override suspend fun delete(vararg obj: T) {
        obj.forEach { item ->
            val firestoreModel = toFirestoreModel(item)
            val query = db.whereEqualTo(idField, firestoreModel[idField]).get().await()
            val doc = query.documents.firstOrNull()
                ?: throw NoSuchElementException("Document with ${firestoreModel[idField]} not found.")

            doc.reference.delete().await()
        }
    }
}