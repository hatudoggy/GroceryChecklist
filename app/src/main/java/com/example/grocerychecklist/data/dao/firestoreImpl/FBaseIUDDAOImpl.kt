package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.BaseDAO
import com.example.grocerychecklist.util.IdGenerator
import kotlinx.coroutines.tasks.await

abstract class FBaseIUDDAOImpl<T : Any>(
    collectionPath: String,
) : FBaseDAOImpl<T>(collectionPath), BaseDAO<T>  {

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