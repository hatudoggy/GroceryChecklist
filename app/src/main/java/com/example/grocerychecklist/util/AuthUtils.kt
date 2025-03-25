package com.example.grocerychecklist.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthUtils {
    private val auth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = auth.currentUser

    init {
        // Listen for authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            if (newUser?.uid != currentUser?.uid) {
                println("🔄 Auth State Changed: Old UID -> ${currentUser?.uid}, New UID -> ${newUser?.uid}")
            }
            currentUser = firebaseAuth.currentUser
        }
    }

    fun isUserLoggedIn(): Boolean {
        return !currentUser?.email.isNullOrBlank()
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

    fun getCurrentUser(): FirebaseUser? {
        return currentUser
    }
}
