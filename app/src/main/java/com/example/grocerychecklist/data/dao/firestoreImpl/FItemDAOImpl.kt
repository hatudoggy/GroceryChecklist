package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.model.ChecklistItem
import com.example.grocerychecklist.data.model.Item
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class FItemDAOImpl: FBaseIUDDAOImpl<Item>(
    FirestoreCollections.ITEMS
), ItemDAO {

    override suspend fun getItemById(itemId: Long): Item {
        TODO("Not yet implemented")
    }

    override fun getAllItems(): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsOrderedByName(): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsOrderedByPrice(): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsByName(qName: String): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsByCategory(category: String): Flow<List<Item>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItemById(itemId: Long): Int {
        TODO("Not yet implemented")
    }

    override fun toFirestoreModel(obj: Item): Map<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): Item {
        TODO("Not yet implemented")
    }

}