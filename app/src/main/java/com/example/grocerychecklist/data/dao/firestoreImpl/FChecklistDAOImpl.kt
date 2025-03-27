package com.example.grocerychecklist.data.dao.firestoreImpl

import android.util.Log
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dto.ChecklistFirestore
import com.example.grocerychecklist.data.model.Checklist
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FChecklistDAOImpl: FBaseIUDDAOImpl<Checklist>(
    FirestoreCollections.CHECKLISTS
), ChecklistDAO {

    override suspend fun getChecklistById(checklistId: Long): Checklist {
        val snapshot = db.document(checklistId.toString()).get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("Checklist with id $checklistId not found.")
        return fromFirestoreModel(snapshot, snapshot.id.toLong())
    }

    override fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>> {
        return db
            .orderBy("lastOpenedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
            }
    }

    override fun toFirestoreModel(obj: Checklist): Map<String, Any?> {
        val firestoreModel = ChecklistFirestore.fromChecklist(obj).toMap()
        return firestoreModel - "id"
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): Checklist {
        val doc = snapshot.toObject(ChecklistFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse checklist data.")
        return doc.toChecklist(id)
    }

    override fun getId(obj: Checklist): Long {
        return obj.id
    }
}