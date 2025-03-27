package com.example.grocerychecklist.data.dao.manager

import com.example.grocerychecklist.data.dao.ChecklistItemDAO
import com.example.grocerychecklist.data.dao.roomImpl.ChecklistItemDAOImpl
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.ChecklistItemFull
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class ChecklistItemDAOManager(
    private val roomDAO: ChecklistItemDAOImpl,
    private val firestoreDAO: ChecklistItemDAO
): ChecklistItemDAO {

    private val useFirestore: Boolean
        get() = Firebase.auth.currentUser?.let { !it.isAnonymous } == true


    override suspend fun getChecklistItemById(checklistItemId: Long): ChecklistItemFull {
        return if (useFirestore) {
            firestoreDAO.getChecklistItemById(checklistItemId)
        } else {
            roomDAO.getChecklistItemById(checklistItemId)
        }
    }

    override fun getAllChecklistItems(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItems(checklistId)
        } else {
            roomDAO.getAllChecklistItems(checklistId)
        }
    }

    override fun getAllChecklistItemsOrderedByOrder(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsOrderedByOrder(checklistId)
        } else {
            roomDAO.getAllChecklistItemsOrderedByOrder(checklistId)
        }
    }

    override fun getAllChecklistItemsBaseOrderedByOrder(checklistId: Long): Flow<List<ChecklistItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
        } else {
            roomDAO.getAllChecklistItemsBaseOrderedByOrder(checklistId)
        }
    }

    override fun getAllChecklistItemsOrderedByName(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsOrderedByName(checklistId)
        } else {
            roomDAO.getAllChecklistItemsOrderedByName(checklistId)
        }
    }

    override fun getAllChecklistItemsOrderedByPrice(checklistId: Long): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsOrderedByPrice(checklistId)
        } else {
            roomDAO.getAllChecklistItemsOrderedByPrice(checklistId)
        }
    }

    override fun getAllChecklistItemsByName(checklistId: Long, qName: String): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsByName(checklistId, qName)
        } else {
            roomDAO.getAllChecklistItemsByName(checklistId, qName)
        }
    }

    override fun getAllChecklistItemsByCategory(checklistId: Long, category: String): Flow<List<ChecklistItemFull>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistItemsByCategory(checklistId, category)
        } else {
            roomDAO.getAllChecklistItemsByCategory(checklistId, category)
        }
    }

    override suspend fun getChecklistItemMaxOrder(checklistId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.getChecklistItemMaxOrder(checklistId)
        } else {
            roomDAO.getChecklistItemMaxOrder(checklistId)
        }
    }

    override suspend fun deleteChecklistById(checklistId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.deleteChecklistById(checklistId)
        } else {
            roomDAO.deleteChecklistById(checklistId)
        }
    }

    override suspend fun deleteChecklistByItemId(itemId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.deleteChecklistByItemId(itemId)
        } else {
            roomDAO.deleteChecklistByItemId(itemId)
        }
    }

    override suspend fun aggregateTotalChecklistItems(checklistId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.aggregateTotalChecklistItems(checklistId)
        } else {
            roomDAO.aggregateTotalChecklistItems(checklistId)
        }
    }

    override suspend fun aggregateTotalChecklistItemPrice(checklistId: Long): Double? {
        return if (useFirestore) {
            firestoreDAO.aggregateTotalChecklistItemPrice(checklistId)
        } else {
            roomDAO.aggregateTotalChecklistItemPrice(checklistId)
        }
    }

    override suspend fun insert(obj: ChecklistItem): Long {
        return if (useFirestore) {
            firestoreDAO.insert(obj)
        } else {
            roomDAO.insert(obj)
        }
    }

    override suspend fun update(obj: ChecklistItem) {
        if (useFirestore) {
            firestoreDAO.update(obj)
        } else {
            roomDAO.update(obj)
        }
    }

    override suspend fun delete(vararg obj: ChecklistItem) {
        if (useFirestore) {
            firestoreDAO.delete(*obj)
        } else {
            roomDAO.delete(*obj)
        }
    }

}