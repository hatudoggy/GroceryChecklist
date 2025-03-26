package com.example.grocerychecklist.data.dao.firestoreImpl

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
        val query = db.whereEqualTo("id", checklistId).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Checklist with id $checklistId not found.")

        return fromFirestoreModel(snapshot)
    }

    override fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>> {
        return db
            .orderBy("lastOpenedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { fromFirestoreModel(it) }
            }
    }

    override fun toFirestoreModel(obj: Checklist): Map<String, Any?> {
        return ChecklistFirestore.fromChecklist(obj).toMap()
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): Checklist {
        val doc = snapshot.toObject(ChecklistFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse checklist data.")
        return doc.toChecklist()
    }
}