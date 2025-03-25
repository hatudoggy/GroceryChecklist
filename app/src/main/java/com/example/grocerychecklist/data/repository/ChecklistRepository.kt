package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.dao.ChecklistDAO
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.util.AuthUtils
import com.example.grocerychecklist.util.TimestampUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ChecklistRepository(
    private val checklistDAO: ChecklistDAO,
    private val firestore: FirebaseFirestore
) {
    private val collectionName = "users"
    private val subCollectionName = "checklists"

    suspend fun addChecklist(checklistInput: ChecklistInput): Long {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val checklist = Checklist(
            name = checklistInput.name,
            description = checklistInput.description,
            icon = checklistInput.icon,
            iconBackgroundColor = checklistInput.iconBackgroundColor,
            createdAt = currentDateTime,
            updatedAt = currentDateTime,
            lastOpenedAt = currentDateTime,
            lastShopAt = currentDateTime
        )

        val checklistId = checklistDAO.insert(checklist)

        // Save to Firestore
        saveChecklistToFirestore(checklist.copy(id = checklistId))

        return checklistId
    }

    suspend fun updateChecklist(id: Long, checklistInput: ChecklistInput) {
        val checklist = checklistDAO.getChecklistById(id)
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedChecklist = checklist.copy(
            name = checklistInput.name,
            description = checklistInput.description,
            icon = checklistInput.icon,
            iconBackgroundColor = checklistInput.iconBackgroundColor,
            updatedAt = currentDateTime
        )

        checklistDAO.update(updatedChecklist)

        // Update in Firestore
        updateChecklistInFirestore(updatedChecklist)
    }

    suspend fun updateChecklistLastOpenedAt(checklistId: Long) {
        val currentDateTime = DateUtility.getCurrentDateTime()
        updateChecklistDate(checklistId, lastOpenedAt = currentDateTime)
    }

    suspend fun updateChecklistLastShoppedAt(checklistId: Long) {
        val currentDateTime = DateUtility.getCurrentDateTime()
        updateChecklistDate(checklistId, lastShopAt = currentDateTime)
    }

    private suspend fun updateChecklistDate(
        checklistId: Long,
        lastOpenedAt: LocalDateTime? = null,
        lastShopAt: LocalDateTime? = null
    ) {
        val checklist = checklistDAO.getChecklistById(checklistId)
        checklist.let {
            val newChecklist = it.copy(
                lastOpenedAt = lastOpenedAt ?: it.lastOpenedAt,
                lastShopAt = lastShopAt ?: it.lastShopAt
            )
            checklistDAO.update(newChecklist)
        }

    }

    suspend fun deleteChecklist(checklist: Checklist) {
        checklistDAO.delete(checklist)

        // Delete from Firestore
        deleteChecklistFromFirestore(checklist)
    }

    suspend fun getChecklist(id: Long): Checklist {
        return checklistDAO.getChecklistById(id)
    }

    fun getChecklists(): Flow<List<Checklist>> {
        return checklistDAO.getAllChecklistsOrderedByLastOpenedAt()
    }

    private suspend fun saveChecklistToFirestore(checklist: Checklist) =
        withContext(Dispatchers.IO) {
            try {
                if (!AuthUtils.isUserLoggedIn()) {
                    throw Exception("User not logged in")
                }
                val documentName = AuthUtils.getAuth().uid.toString()
                val documentReference = firestore.collection(collectionName).document(documentName)
                    .collection(subCollectionName).document()
                val firestoreChecklist = checklist
                documentReference.set(firestoreChecklist).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun updateChecklistInFirestore(checklist: Checklist) =
        withContext(Dispatchers.IO) {
            try {
                if (!AuthUtils.isUserLoggedIn()) {
                    throw Exception("User not logged in")
                }

                val documentName = AuthUtils.getAuth().uid.toString()
                firestore.collection(collectionName).document(documentName)
                    .collection(subCollectionName).whereEqualTo("id", checklist.id).get()
                    .await().documents.firstOrNull()?.reference?.set(checklist)?.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun deleteChecklistFromFirestore(checklist: Checklist) =
        withContext(Dispatchers.IO) {
            try {
                if (!AuthUtils.isUserLoggedIn()) {
                    throw Exception("User not logged in")
                }

                val documentName = AuthUtils.getAuth().uid.toString()
                firestore.collection(collectionName).document(documentName)
                    .collection(subCollectionName).whereEqualTo("id", checklist.id).get()
                    .await().documents.firstOrNull()?.reference?.delete()?.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun saveUnsyncedChecklists() =
        withContext(Dispatchers.IO) {
            try {
                if (!AuthUtils.isUserLoggedIn()) {
                    throw Exception("User not logged in")
                }

                val documentName = AuthUtils.getAuth().uid.toString()

                val roomDbChecklists = getChecklists().first()

                // Get saved items from Firestore
                val snapshot = firestore.collection(collectionName)
                    .document(documentName)
                    .collection(subCollectionName)
                    .get()
                    .await()

                var nextId = snapshot.documents.size + 1L // Start from the next available ID

                if (roomDbChecklists.isNotEmpty()) {
                    val newChecklists = roomDbChecklists.map { checklist ->
                        checklist.copy(id = nextId++)
                    }

                    newChecklists.forEach { checklist ->
                        saveChecklistToFirestore(checklist)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun fetchChecklistsFromFirestoreAndInsertToRoom(
    ) = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                throw Exception("User not logged in")
            }

            val documentName = AuthUtils.getAuth().uid.toString()

            val snapshot = firestore.collection(collectionName)
                .document(documentName)
                .collection(subCollectionName)
                .get()
                .await()

            val firestoreChecklists = snapshot.documents.mapNotNull { doc ->
                parseFirestoreDocument(doc)
            }

            // Clear existing checklists in the Room database
            // since we are fetching a new set
            checklistDAO.clearAll()

            firestoreChecklists.forEach { checklist ->
                checklistDAO.insert(checklist)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseFirestoreDocument(doc: DocumentSnapshot): Checklist? {
        return try {
            val id = doc.getLong("id") ?: return null
            val name = doc.getString("name").orEmpty()
            val description = doc.getString("description").orEmpty()
            val icon = doc.getString("icon")?.let { enumValueOf<IconOption>(it) } ?: IconOption.MAIN_GROCERY
            val iconBackgroundColor = doc.getString("iconBackgroundColor")?.let { enumValueOf<ColorOption>(it) } ?: ColorOption.White

            val createdAt = TimestampUtil.parseTimestamp(doc.get("createdAt") as? Map<*, *>)
            val updatedAt = TimestampUtil.parseTimestamp(doc.get("updatedAt") as? Map<*, *>)
            val lastOpenedAt = TimestampUtil.parseTimestamp(doc.get("lastOpenedAt") as? Map<*, *>)
            val lastShopAt = TimestampUtil.parseTimestamp(doc.get("lastShopAt") as? Map<*, *>)

            Checklist(
                id,
                name,
                description,
                icon,
                iconBackgroundColor,
                createdAt,
                updatedAt,
                lastOpenedAt,
                lastShopAt
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}