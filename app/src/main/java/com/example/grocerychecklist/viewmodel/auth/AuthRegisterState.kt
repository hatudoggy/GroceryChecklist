package com.example.grocerychecklist.viewmodel.auth

import java.lang.Error

data class AuthRegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isConfirmPasswordValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false
)