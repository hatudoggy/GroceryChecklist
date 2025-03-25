package com.example.grocerychecklist.data.repository

import com.example.grocerychecklist.data.dao.ItemDAO
import com.example.grocerychecklist.data.mapper.ItemInput
import com.example.grocerychecklist.data.model.Item
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

enum class ItemOrder {
    CreatedAt, Name, Price,
}

class ItemRepository(
    private val itemDAO: ItemDAO,
    private val firestore: FirebaseFirestore
) {
    private val collectionName = "users"
    private val subCollectionName = "items"

    suspend fun addItem(itemInput: ItemInput): Long {
        val currentDateTime = DateUtility.getCurrentDateTime()

        val item = Item(
            name = itemInput.name,
            price = itemInput.price,
            category = itemInput.category,
            measureType = itemInput.measureType,
            measureValue = itemInput.measureValue,
            photoRef = itemInput.photoRef,
            createdAt = currentDateTime,
            updatedAt = currentDateTime
        )

        val itemId = itemDAO.insert(item)

        // Save to Firestore
        saveItemToFirestore(item.copy(id = itemId))

        return itemId
    }

    suspend fun updateItem(id: Long, itemInput: ItemInput) {
        val item = itemDAO.getItemById(id)
        val currentDateTime = DateUtility.getCurrentDateTime()

        val updatedItem = item.copy(
            name = itemInput.name,
            price = itemInput.price,
            category = itemInput.category,
            measureType = itemInput.measureType,
            measureValue = itemInput.measureValue,
            photoRef = itemInput.photoRef,
            updatedAt = currentDateTime
        )

        itemDAO.update(updatedItem)

        // Update in Firestore
        updateItemInFirestore(updatedItem)
    }

    suspend fun deleteItem(item: Item) {
        itemDAO.delete(item)

        // Delete from Firestore
        deleteItemFromFirestore(item)
    }

    suspend fun getItem(id: Long): Item {
        return itemDAO.getItemById(id)
    }

    fun getItems(orderBy: ItemOrder): Flow<List<Item>> {
        return when (orderBy) {
            ItemOrder.CreatedAt -> itemDAO.getAllItemsOrderedByCreatedAt()
            ItemOrder.Name -> itemDAO.getAllItemsOrderedByName()
            ItemOrder.Price -> itemDAO.getAllItemsOrderedByPrice()
        }
    }

    fun searchItems(searchQuery: String): Flow<List<Item>> {
        return itemDAO.getAllItemsByName(searchQuery)
    }

    private suspend fun saveItemToFirestore(item: Item) = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                throw Exception("User not logged in")
            }
            val documentName = AuthUtils.getAuth().uid.toString()
            val documentReference = firestore.collection(collectionName).document(documentName)
                .collection(subCollectionName).document()
            val firestoreItem = item
            documentReference.set(firestoreItem).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun updateItemInFirestore(item: Item) = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                throw Exception("User not logged in")
            }

            val documentName = AuthUtils.getAuth().uid.toString()
            firestore.collection(collectionName).document(documentName)
                .collection(subCollectionName).whereEqualTo("id", item.id).get()
                .await().documents.firstOrNull()?.reference?.set(item)?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun deleteItemFromFirestore(item: Item) = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                throw Exception("User not logged in")
            }

            val documentName = AuthUtils.getAuth().uid.toString()
            firestore.collection(collectionName).document(documentName)
                .collection(subCollectionName).whereEqualTo("id", item.id).get()
                .await().documents.firstOrNull()?.reference?.delete()?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveUnsyncedItems() = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                throw Exception("User not logged in")
            }

            val documentName = AuthUtils.getAuth().uid.toString()

            val roomDbItems = getItems(ItemOrder.CreatedAt).first()

            // Get saved items from Firestore
            val snapshot = firestore.collection(collectionName)
                .document(documentName)
                .collection(subCollectionName)
                .get()
                .await()

            var nextId = snapshot.documents.size + 1L // Start from the next available ID

            if (roomDbItems.isNotEmpty()) {
                val newItems = roomDbItems.map { item ->
                    item.copy(id = nextId++ )
                }

                newItems.forEach { item ->
                    saveItemToFirestore(item)
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun fetchItemsFromFirestoreAndInsertToRoom(

    ) = withContext(Dispatchers.IO) {
        try {
            if (!AuthUtils.isUserLoggedIn()) {
                println("Firestore Query Result: LOOOL")
                throw Exception("User not logged in")
            }

            val documentName = AuthUtils.getAuth().uid.toString()

            val snapshot = firestore.collection(collectionName)
                .document(documentName)
                .collection(subCollectionName)
                .get()
                .await()

            println("Firestore Query Result: $collectionName, $documentName, $subCollectionName")

            println("Firestore Query Result: ${snapshot.documents}")

            val firestoreItems = snapshot.documents.mapNotNull { doc ->
                parseFirestoreDocument(doc)
            }

            println("Firestore Query Result: $firestoreItems")

            // Clear existing items in the Room database
            // since we are fetching a new set
            itemDAO.clearAll()

            firestoreItems.forEach { item ->
                itemDAO.insert(item)
            }
        } catch (e: Exception) {
            println("Firestore Query Failed: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    private fun parseFirestoreDocument(doc: DocumentSnapshot): Item? {
        return try {
            val id = doc.getLong("id") ?: return null
            val name = doc.getString("name").orEmpty()
            val price = doc.getDouble("price") ?: 0.0
            val category = doc.getString("category").orEmpty()
            val measureType = doc.getString("measureType").orEmpty()
            val measureValue = doc.getDouble("measureValue") ?: 0.0
            val photoRef = doc.getString("photoRef").orEmpty()

            val createdAt = TimestampUtil.parseTimestamp(doc.get("createdAt") as? Map<*, *>)
            val updatedAt = TimestampUtil.parseTimestamp(doc.get("updatedAt") as? Map<*, *>)

            Item(
                id,
                name,
                price,
                category,
                measureType,
                measureValue,
                photoRef,
                createdAt,
                updatedAt
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}