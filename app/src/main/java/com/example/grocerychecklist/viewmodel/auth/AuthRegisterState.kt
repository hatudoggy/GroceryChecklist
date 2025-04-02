package com.example.grocerychecklist.viewmodel.auth

import com.example.grocerychecklist.viewmodel.util.SubmissionState
import java.lang.Error

data class AuthRegisterState(
    // Form fields
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Validation
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Submission state
    val submissionState: SubmissionState = SubmissionState.Idle
) {
    val isFormValid: Boolean
        get() = emailError == null && passwordError == null && confirmPasswordError == null && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
}