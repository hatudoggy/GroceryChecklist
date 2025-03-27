package com.example.grocerychecklist.viewmodel.auth

data class AuthLoginState (
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isFormValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)