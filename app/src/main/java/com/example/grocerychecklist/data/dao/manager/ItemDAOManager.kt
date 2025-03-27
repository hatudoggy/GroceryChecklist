package com.example.grocerychecklist.data.dao.manager

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.dao.roomImpl.ItemDAOImpl
import com.example.grocerychecklist.data.model.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class ItemDAOManager(
    private val roomDAO: ItemDAOImpl,
    private val firestoreDAO: ItemDAO
) : ItemDAO {

    private val useFirestore: Boolean
        get() = Firebase.auth.currentUser?.let { !it.isAnonymous } == true


    override suspend fun getItemById(itemId: Long): Item {
        return if (useFirestore) {
            firestoreDAO.getItemById(itemId)
        } else {
            roomDAO.getItemById(itemId)
        }
    }

    override fun getAllItems(): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItems()
        } else {
            roomDAO.getAllItems()
        }
    }

    override fun getAllItemsOrderedByName(): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItemsOrderedByName()
        } else {
            roomDAO.getAllItemsOrderedByName()
        }
    }

    override fun getAllItemsOrderedByPrice(): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItemsOrderedByPrice()
        } else {
            roomDAO.getAllItemsOrderedByPrice()
        }
    }

    override fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItemsOrderedByCreatedAt()
        } else {
            roomDAO.getAllItemsOrderedByCreatedAt()
        }
    }

    override fun getAllItemsByName(qName: String): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItemsByName(qName)
        } else {
            roomDAO.getAllItemsByName(qName)
        }
    }

    override fun getAllItemsByCategory(category: String): Flow<List<Item>> {
        return if (useFirestore) {
            firestoreDAO.getAllItemsByCategory(category)
        } else {
            roomDAO.getAllItemsByCategory(category)
        }
    }

    override suspend fun deleteItemById(itemId: Long): Int {
        return if (useFirestore) {
            firestoreDAO.deleteItemById(itemId)
        } else {
            roomDAO.deleteItemById(itemId)
        }
    }

    override suspend fun insert(obj: Item): Long {
        return if (useFirestore) {
            firestoreDAO.insert(obj)
        } else {
            roomDAO.insert(obj)
        }
    }

    override suspend fun update(obj: Item) {
        return if (useFirestore) {
            firestoreDAO.update(obj)
        } else {
            roomDAO.update(obj)
        }
    }

    override suspend fun delete(vararg obj: Item) {
        return if (useFirestore) {
            firestoreDAO.delete(*obj)
        } else {
            roomDAO.delete(*obj)
        }
    }

}