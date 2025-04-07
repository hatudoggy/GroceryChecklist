package com.example.grocerychecklist.data.dao

import android.content.Context
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FChecklistItemDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FHistoryDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FHistoryItemDAOImpl
import com.example.grocerychecklist.data.dao.firestoreImpl.FItemDAOImpl


abstract class DAOProvider {
    abstract fun getChecklistDAO(): ChecklistDAO
    abstract fun getChecklistItemDAO(): ChecklistItemDAO
    abstract fun getItemDAO(): ItemDAO
    abstract fun getHistoryDAO(): HistoryDAO
    abstract fun getHistoryItemDAO(): HistoryItemDAO

    companion object {
        fun createProvider(context: Context, useFirestore: Boolean): DAOProvider {
            return if (useFirestore) {
                FirestoreDAOProvider()
            } else {
                RoomDAOProvider(AppDatabase.getDatabase(context))
            }
        }
    }
}

class RoomDAOProvider(private val db: AppDatabase) : DAOProvider() {
    override fun getChecklistDAO(): ChecklistDAO = db.checklistDAO()
    override fun getChecklistItemDAO(): ChecklistItemDAO = db.checklistItemDAO()
    override fun getItemDAO(): ItemDAO = db.itemDAO()
    override fun getHistoryDAO(): HistoryDAO = db.historyDAO()
    override fun getHistoryItemDAO(): HistoryItemDAO = db.historyItemDAO()
}

class FirestoreDAOProvider : DAOProvider() {
    override fun getChecklistDAO(): ChecklistDAO = FChecklistDAOImpl()
    override fun getChecklistItemDAO(): ChecklistItemDAO = FChecklistItemDAOImpl()
    override fun getItemDAO(): ItemDAO = FItemDAOImpl()
    override fun getHistoryDAO(): HistoryDAO = FHistoryDAOImpl()
    override fun getHistoryItemDAO(): HistoryItemDAO = FHistoryItemDAOImpl()
}