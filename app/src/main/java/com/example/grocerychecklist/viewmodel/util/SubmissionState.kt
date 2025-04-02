package com.example.grocerychecklist.viewmodel.util

sealed interface SubmissionState {
    object Idle : SubmissionState
    object Loading : SubmissionState
    data class Error(val message: String) : SubmissionState
    object Success : SubmissionState
}