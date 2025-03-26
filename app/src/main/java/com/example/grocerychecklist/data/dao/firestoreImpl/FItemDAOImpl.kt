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

    private val fChecklistItemDaoImpl: FChecklistItemDAOImpl by lazy {
        FChecklistItemDAOImpl()
    }

    override suspend fun getItemById(itemId: Long): Item {
        val query = db.whereEqualTo("id", itemId).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Checklist with id $itemId not found")

        return fromFirestoreModel(snapshot)
    }

    override suspend fun getItemByName(name: String): Item {
        val query = db.whereEqualTo("name", name).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Item with name $name not found")

        return fromFirestoreModel(snapshot)
    }

    override suspend fun getItemByCategory(category: String): Item {
        val query = db.whereEqualTo("category", category).get().await()
        val snapshot = query.documents.firstOrNull()
            ?: throw NoSuchElementException("Item with category $category not found")

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

        val itemToDelete = querySnapshot.documents.first()

        // Firestore item model
        val item = fromFirestoreModel(itemToDelete)

        db.document(itemToDelete.id).delete().await()

        // Also delete checklist item entries of this item
        fChecklistItemDaoImpl.deleteChecklistByItemId(item.id)

        return querySnapshot.size()
    }

    override suspend fun deleteItemByIds(vararg item: Item) {
        item.forEach { item ->
            val querySnapshot = db.whereEqualTo("id", item.id).get().await()

            if (querySnapshot.isEmpty) {
                throw NoSuchElementException("Item with id ${item.id} not found")
            }

            val itemToDelete = querySnapshot.documents.first()

            // Firestore item model
            val item = fromFirestoreModel(itemToDelete)

            db.document(itemToDelete.id).delete().await()

            // Also delete checklist item entries of this item
            fChecklistItemDaoImpl.deleteChecklistByItemId(item.id)
        }
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