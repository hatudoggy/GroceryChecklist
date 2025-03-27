package com.example.grocerychecklist.data.model.service

import com.example.grocerychecklist.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountService{
    val currentUser: Flow<User?>
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

    fun getUserProfile(): User {
        return Firebase.auth.currentUser.toAppUser()
    }

    suspend fun createAnonymousAccount() {
        if (Firebase.auth.currentUser == null) {
            Firebase.auth.signInAnonymously().await()
        }
    }

    suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).await()
    }

    suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
    }

    suspend fun linkAccountWithEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        Firebase.auth.currentUser!!.linkWithCredential(credential).await()
    }

    suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    suspend fun signInWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signOut() {
        Firebase.auth.signOut()

        // If user is anonymous, do not sign them out
        if (Firebase.auth.currentUser?.isAnonymous == true) return

        Firebase.auth.signOut()

        // Optional: Automatically sign back in anonymously if no user exists
        if (Firebase.auth.currentUser == null) {
            createAnonymousAccount()
        }
    }

    suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
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
        val user = Firebase.auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential).await()
    }

    private fun FirebaseUser?.toAppUser(): User {
        return if (this == null) User() else User(
            id = this.uid,
            email = this.email ?: "",
            provider = this.providerId,
            displayName = this.displayName ?: "",
            isAnonymous = this.isAnonymous
        )
    }
}