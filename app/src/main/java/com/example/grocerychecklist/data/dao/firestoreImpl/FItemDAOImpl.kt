package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.dto.ItemFirestore
import com.example.grocerychecklist.data.model.Item
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FItemDAOImpl : FBaseIUDDAOImpl<Item>(
    FirestoreCollections.ITEMS
), ItemDAO {

    override suspend fun getItemById(itemId: Long): Item {
        val query = db.whereEqualTo("id", itemId).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Checklist with id $itemId not found")

        return fromFirestoreModel(snapshot)
    }

    override fun getAllItems(): Flow<List<Item>> {
        return db.snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override fun getAllItemsOrderedByName(): Flow<List<Item>> {
        return db.orderBy("name").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override fun getAllItemsOrderedByPrice(): Flow<List<Item>> {
        return db.orderBy("price").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>> {
        return db.orderBy("createdAt").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override fun getAllItemsByName(qName: String): Flow<List<Item>> {
        return db.whereEqualTo("name", qName).snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override fun getAllItemsByCategory(category: String): Flow<List<Item>> {
        return db.whereEqualTo("category", category).snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it) }
        }
    }

    override suspend fun deleteItemById(itemId: Long): Int {
        val querySnapshot = db.whereEqualTo("id", itemId).get().await()

        if (querySnapshot.isEmpty) {
            throw NoSuchElementException("Item with id $itemId not found")
        }

        querySnapshot.documents.forEach {
            db.document(it.reference.path).delete().await()
        }

        return querySnapshot.size()
    }

    override fun toFirestoreModel(obj: Item): Map<String, Any?> {
        return ItemFirestore.fromItem(obj).toMap()
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot): Item {
        val doc = snapshot.toObject(ItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse item data.")

        return doc.toItem()
    }

}