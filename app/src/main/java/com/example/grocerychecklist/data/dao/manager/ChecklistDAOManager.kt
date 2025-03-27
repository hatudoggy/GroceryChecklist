package com.example.grocerychecklist.data.dao.manager

import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.dao.roomImpl.ChecklistDAOImpl
import com.example.grocerychecklist.data.model.Checklist
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class ChecklistDAOManager(
    private val roomDAO: ChecklistDAOImpl,
    private val firestoreDAO: ChecklistDAO
): ChecklistDAO {

    private val useFirestore: Boolean
        get() = Firebase.auth.currentUser?.let { !it.isAnonymous } == true


    override suspend fun getChecklistById(checklistId: Long): Checklist {
        return if (useFirestore) {
            firestoreDAO.getChecklistById(checklistId)
        } else {
            roomDAO.getChecklistById(checklistId)
        }
    }

    override fun getAllChecklistsOrderedByLastOpenedAt(): Flow<List<Checklist>> {
        return if (useFirestore) {
            firestoreDAO.getAllChecklistsOrderedByLastOpenedAt()
        } else {
            roomDAO.getAllChecklistsOrderedByLastOpenedAt()
        }
    }

    override suspend fun insert(obj: Checklist): Long {
        return if (useFirestore) {
            firestoreDAO.insert(obj)
        } else {
            roomDAO.insert(obj)
        }
    }

    override suspend fun update(obj: Checklist) {
        if (useFirestore) {
            firestoreDAO.update(obj)
        } else {
            roomDAO.update(obj)
        }
    }

    override suspend fun delete(vararg obj: Checklist) {
        if (useFirestore) {
            firestoreDAO.delete(*obj)
        } else {
            roomDAO.delete(*obj)
        }
    }

}