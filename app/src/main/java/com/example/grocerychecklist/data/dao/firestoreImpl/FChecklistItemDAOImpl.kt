package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class FChecklistItemDAOImpl: FBaseIUDDAOImpl<ChecklistItem>(
    FirestoreCollections.CHECKLISTS
), ChecklistItemDAO {

    override suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsByName(
        checklistId: Long,
        qName: String
    ): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override fun getAllChecklistItemsByCategory(
        checklistId: Long,
        category: String
    ): Flow<List<ChecklistItemFull>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChecklistItemMaxOrder(checklistId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChecklistById(checklistId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun aggregateTotalChecklistItems(checklistId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun aggregateTotalChecklistItemPrice(checklistId: Long): Double? {
        TODO("Not yet implemented")
    }

    override fun toFirestoreModel(obj: ChecklistItem): Map<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): ChecklistItem {
        TODO("Not yet implemented")
    }

}