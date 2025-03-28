package com.example.grocerychecklist.data.dao.firestoreImpl

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.dto.ItemFirestore
import com.example.grocerychecklist.data.model.Item
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface IFItemDAOImpl : ItemDAO {
    suspend fun getItemByName(name: String): Item

    suspend fun getItemByCategory(category: String): Item
}

class FItemDAOImpl : FBaseIUDDAOImpl<Item>(
    FirestoreCollections.ITEMS
), IFItemDAOImpl {

    override suspend fun getItemById(itemId: Long): Item {
        val snapshot = db.document(itemId.toString()).get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("Item with id $itemId not found.")
        return fromFirestoreModel(snapshot, itemId)
    }

    override suspend fun getItemByName(name: String): Item {
        val query = db.whereEqualTo("name", name).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Item with name $name not found")
        return fromFirestoreModel(snapshot, snapshot.id.toLong())
    }

    override suspend fun getItemByCategory(category: String): Item {
        val query = db.whereEqualTo("category", category).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Item with category $category not found")
        return fromFirestoreModel(snapshot, snapshot.id.toLong())
    }

    override fun getAllItems(): Flow<List<Item>> {
        return db.snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override fun getAllItemsOrderedByName(): Flow<List<Item>> {
        return db.orderBy("name").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override fun getAllItemsOrderedByPrice(): Flow<List<Item>> {
        return db.orderBy("price").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override fun getAllItemsOrderedByCreatedAt(): Flow<List<Item>> {
        return db.orderBy("createdAt").snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override fun getAllItemsByName(qName: String): Flow<List<Item>> {
        return db.whereEqualTo("name", qName).snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override fun getAllItemsByCategory(category: String): Flow<List<Item>> {
        return db.whereEqualTo("category", category).snapshots().map { querySnapshot ->
            querySnapshot.documents.map { fromFirestoreModel(it, it.id.toLong()) }
        }
    }

    override suspend fun deleteItemById(itemId: Long): Int {
        val snapshot = db.document(itemId.toString()).get().await()
        if (!snapshot.exists())
            throw NoSuchElementException("Item with id $itemId not found.")
        db.document(itemId.toString()).delete()
        return 1
    }

    override fun toFirestoreModel(obj: Item): Map<String, Any?> {
        val firestoreModel = ItemFirestore.fromItem(obj).toMap()
        return firestoreModel - "id"
    }

    override fun fromFirestoreModel(snapshot: DocumentSnapshot, id: Long): Item {
        val doc = snapshot.toObject(ItemFirestore::class.java)
            ?: throw IllegalStateException("Failed to parse item data.")
        return doc.toItem(id)
    }

    override fun getId(obj: Item): Long {
        return obj.id
    }
}