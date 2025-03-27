package com.example.grocerychecklist.data.dao.manager

import com.example.grocerychecklist.data.dao.HistoryItemDAO
import com.example.grocerychecklist.data.dao.roomImpl.HistoryItemDAOImpl
import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.example.grocerychecklist.data.model.HistoryItem
import com.example.grocerychecklist.data.repository.ChecklistItemOrder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class HistoryItemDAOManager(
    private val roomDAO: HistoryItemDAOImpl,
    private val firestoreDAO: HistoryItemDAO
): HistoryItemDAO {

    private val useFirestore: Boolean
        get() = Firebase.auth.currentUser?.let { !it.isAnonymous } == true


    override suspend fun insertBatch(historyItems: List<HistoryItem>): List<Long> {
        return if (useFirestore) {
            firestoreDAO.insertBatch(historyItems)
        } else {
            roomDAO.insertBatch(historyItems)
        }
    }

    override suspend fun getHistoryItemById(historyItemId: Long): HistoryItem {
        return if (useFirestore) {
            firestoreDAO.getHistoryItemById(historyItemId)
        } else {
            roomDAO.getHistoryItemById(historyItemId)
        }
    }

    override fun getAllHistoryItems(historyId: Long): Flow<List<HistoryItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoryItems(historyId)
        } else {
            roomDAO.getAllHistoryItems(historyId)
        }
    }

    override fun getAllHistoryItemsOrderFilter(
        historyId: Long,
        order: ChecklistItemOrder
    ): Flow<List<HistoryItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoryItemsOrderFilter(historyId, order)
        } else {
            roomDAO.getAllHistoryItemsOrderFilter(historyId, order)
        }
    }

    override fun getAllHistoryItemsOrderAndCheckedFilter(
        historyId: Long,
        order: ChecklistItemOrder,
        isChecked: Boolean
    ): Flow<List<HistoryItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoryItemsOrderAndCheckedFilter(historyId, order, isChecked)
        } else {
            roomDAO.getAllHistoryItemsOrderAndCheckedFilter(historyId, order, isChecked)
        }
    }

    override fun getAllHistoryItemsByName(historyId: Long, qName: String): Flow<List<HistoryItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoryItemsByName(historyId, qName)
        } else {
            roomDAO.getAllHistoryItemsByName(historyId, qName)
        }
    }

    override fun getAllHistoryItemsByCategory(
        historyId: Long,
        category: Locale.Category
    ): Flow<List<HistoryItem>> {
        return if (useFirestore) {
            firestoreDAO.getAllHistoryItemsByCategory(historyId, category)
        } else {
            roomDAO.getAllHistoryItemsByCategory(historyId, category)
        }
    }

    override suspend fun aggregateTotalHistoryItems(historyId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.aggregateTotalHistoryItems(historyId)
        } else {
            roomDAO.aggregateTotalHistoryItems(historyId)
        }
    }

    override suspend fun aggregateTotalHistoryItemPrice(historyId: Long): Double? {
        return if (useFirestore) {
            firestoreDAO.aggregateTotalHistoryItemPrice(historyId)
        } else {
            roomDAO.aggregateTotalHistoryItemPrice(historyId)
        }
    }

    override fun aggregateTotalPriceMonth(date: String): Flow<Double?> {
        return if (useFirestore) {
            firestoreDAO.aggregateTotalPriceMonth(date)
        } else {
            roomDAO.aggregateTotalPriceMonth(date)
        }
    }

    override fun aggregateCategoryBreakdownMonth(date: String): Flow<List<HistoryItemAggregated>> {
        return if (useFirestore) {
            firestoreDAO.aggregateCategoryBreakdownMonth(date)
        } else {
            roomDAO.aggregateCategoryBreakdownMonth(date)
        }
    }

}