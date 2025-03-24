package com.example.grocerychecklist.util

import com.google.firebase.auth.FirebaseAuth

object AuthUtils {
    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return !auth.currentUser?.email.isNullOrBlank()
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }
}
