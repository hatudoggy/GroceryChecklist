package com.example.grocerychecklist.data.service

import com.example.grocerychecklist.data.dao.firestoreImpl.FirestoreCollections
import com.example.grocerychecklist.data.model.AuthUser
import com.example.grocerychecklist.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountService{
    val currentAuthUser: Flow<AuthUser?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser.toAppUser())
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isAnonymous(): Boolean {
        return Firebase.auth.currentUser?.isAnonymous ?: false
    }

    fun getUserProfile(): AuthUser {
        return Firebase.auth.currentUser.toAppUser()
    }

    suspend fun createAnonymousAccount() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || !currentUser.isAnonymous) {
            if (currentUser != null) Firebase.auth.signOut() // Sign out if a non-anonymous user is signed in
            Firebase.auth.signInAnonymously().await()        }
    }

    suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).await()
    }

    suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
            createUserDocumentIfNotExists()
        } catch (e: Exception) {
            throw Exception("User is already linked with another account.")
        }
    }

    suspend fun linkAccountWithEmail(email: String, password: String) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            createUserDocumentIfNotExists()
        } catch (e: Exception) {
            throw Exception("User is already linked with another account.")
        }

    }

    suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    suspend fun signInWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    suspend fun resetPassword() {
        val email = Firebase.auth.currentUser?.email

        if (email.isNullOrEmpty()) {
            throw IllegalStateException("No email found for the current user")
        }

        Firebase.auth.sendPasswordResetEmail(email.toString()).await()
    }

    suspend fun updateEmail(newEmail: String) {
        Firebase.auth.currentUser!!.verifyBeforeUpdateEmail(newEmail).await()

    }

    suspend fun reauthenticate(password: String) {
        val user = Firebase.auth.currentUser ?: throw Exception("No authenticated user")
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential).await()
    }

    private suspend fun createUserDocumentIfNotExists() {
        val currentUser = Firebase.auth.currentUser
        val userId = currentUser?.uid ?: return
        val userRef = Firebase.firestore.collection(FirestoreCollections.USERS).document(userId)

        val snapshot = userRef.get().await()
        if (!snapshot.exists()) {
            val newUser = User(
                email = currentUser.email ?: "",
                displayName = currentUser.displayName ?: ""
            )
            userRef.set(newUser).await()
        }

    }

    private fun FirebaseUser?.toAppUser(): AuthUser {
        return if (this == null) AuthUser() else AuthUser(
            id = this.uid,
            email = this.email ?: "",
            provider = this.providerId,
            displayName = this.displayName ?: "",
            isAnonymous = this.isAnonymous
        )
    }
}