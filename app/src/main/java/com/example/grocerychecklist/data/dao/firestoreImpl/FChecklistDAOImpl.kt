package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dto.ChecklistFirestore
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.data.repository.ChecklistDetails
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FChecklistDAOImpl: FBaseIUDDAOImpl<Checklist>(
    FirestoreCollections.CHECKLISTS
), ChecklistDAO {

    override fun getChecklistById(checklistId: Long): Flow<Checklist> {
        return flow {
            val snapshot = db.document(checklistId.toString()).get().await()
            if (!snapshot.exists())
                throw NoSuchElementException("Checklist with id $checklistId not found.")

            emit(fromFirestoreModel(snapshot, checklistId))
        }
    }

    override fun getChecklistWithDetails(checklistId: Long): Flow<ChecklistDetails> {
        return flow {
            val checklist = getChecklistById(checklistId).first()
            val checklistItems = FChecklistItemDAOImpl().getAllChecklistItems(checklistId)
            val itemCount = checklistItems.first().size
            val totalPrice = checklistItems.first().sumOf { it.item.price * it.checklistItem.quantity }

            emit(ChecklistDetails(
                checklist.id,
                checklist.name,
                checklist.description,
                checklist.icon,
                checklist.iconBackgroundColor,
                checklist.createdAt,
                checklist.updatedAt,
                checklist.lastOpenedAt,
                checklist.lastShopAt,
                itemCount = itemCount,
                totalPrice = totalPrice
            ))
        }
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