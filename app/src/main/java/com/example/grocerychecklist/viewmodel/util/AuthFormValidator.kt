package com.example.grocerychecklist.viewmodel.util

import android.util.Patterns

class AuthFormValidator {
    companion object {
        fun validateEmail(email: String): ValidationResult {
            return if (email.isBlank()) {
                ValidationResult(error = "Email cannot be empty")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ValidationResult(error = "Invalid email format")
            } else {
                ValidationResult(isValid = true)
            }
        }

        fun validatePassword(password: String): ValidationResult {
            return when {
                password.isBlank() -> ValidationResult(error = "Password cannot be empty")
                password.length < MIN_PASS_LENGTH -> ValidationResult(error = "Password too short")
                !password.matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z]).*$")) ->
                    ValidationResult(error = "Must contain upper and lowercase letters, numbers, and symbols")
                else -> ValidationResult(isValid = true)
            }
        }

        fun validateNewPassword(password: String): ValidationResult {
            val passwordError = when {
                password.isBlank() -> "Password cannot be empty"
                password.length < MIN_PASS_LENGTH -> "Password must be at least $MIN_PASS_LENGTH characters"
                else -> {
                    val missingRequirements = mutableListOf<String>()
                    if (!password.matches(Regex(".*\\d.*"))) {
                        missingRequirements.add("number")
                    }
                    if (!password.matches(Regex(".*[a-z].*"))) {
                        missingRequirements.add("lowercase")
                    }
                    if (!password.matches(Regex(".*[A-Z].*"))) {
                        missingRequirements.add("uppercase")
                    }
                    if (!password.matches(Regex(".*[^a-zA-Z\\d\\s].*"))) {
                        missingRequirements.add("special character")
                    }

                    if (missingRequirements.isNotEmpty()) {
                        "Missing: ${missingRequirements.joinToString(", ")}"
                    } else {
                        null
                    }
                }
            }

            return passwordError?.let { ValidationResult(error = it) } ?: ValidationResult(isValid = true)
        }

        fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
            return if (password != confirmPassword) ValidationResult(error = "Passwords do not match") else ValidationResult(isValid = true)
        }
    }
}

data class ValidationResult(val isValid: Boolean = false, val error: String? = null)