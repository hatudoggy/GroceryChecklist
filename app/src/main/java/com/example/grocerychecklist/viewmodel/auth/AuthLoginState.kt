package com.example.grocerychecklist.viewmodel.auth

import com.example.grocerychecklist.viewmodel.util.SubmissionState

data class AuthLoginState (
    // Form fields
    val email: String = "",
    val password: String = "",

    // Validation
    val emailError: String? = null,
    val passwordError: String? = null,

    // Submission state
    val submissionState: SubmissionState = SubmissionState.Idle
) {
    val isFormValid: Boolean
        get() = emailError == null && passwordError == null && email.isNotBlank() && password.isNotBlank()
}

