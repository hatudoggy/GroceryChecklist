package com.example.grocerychecklist.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.util.ERROR_TAG
import com.example.grocerychecklist.util.UNEXPECTED_CREDENTIAL
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.util.DEFAULT_ERROR
import com.example.grocerychecklist.util.NetworkUtils
import com.example.grocerychecklist.util.TIMEOUT_ERROR
import com.example.grocerychecklist.viewmodel.util.AuthFormValidator
import com.example.grocerychecklist.viewmodel.util.SubmissionState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

class AuthLoginViewModel(
    private val navigator: Navigator,
    private val accountService: AccountService,
    private val application: Application
): ViewModel() {
    // Add a state flow to manage the UI state of the registration process
    private val _uiState = MutableStateFlow(AuthLoginState())
    val uiState: StateFlow<AuthLoginState> = _uiState.asStateFlow()

    private fun updateEmailField(newEmail: String) {
        val emailValidation = AuthFormValidator.validateEmail(newEmail)
        _uiState.update { currentState ->
            currentState.copy(
                email = newEmail,
                emailError = emailValidation.error
            )
        }
    }

    private fun updatePasswordField(newPassword: String) {
        val passwordValidation = AuthFormValidator.validatePassword(newPassword)
        _uiState.update { currentState ->
            currentState.copy(
                password = newPassword,
                passwordError = passwordValidation.error
            )
        }
    }


    private fun onLogInClick() {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(submissionState = SubmissionState.Loading) }

            try{
                withTimeout(5000){
                    accountService.signInWithEmail(_uiState.value.email, _uiState.value.password)
                    _uiState.update { it.copy(submissionState = SubmissionState.Success) }
                }
            }
            catch (_: TimeoutCancellationException) {
                _uiState.update { it.copy(submissionState = SubmissionState.Error(TIMEOUT_ERROR)) }
            }
            catch (_: Exception) {
                _uiState.update { it.copy(submissionState = SubmissionState.Error("Invalid email or password")) }
            }
        }
    }

    private fun onGoogleLoginClick(credential: Credential) {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(submissionState = SubmissionState.Loading) }

            try {
                withTimeout(5000) {
                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                        _uiState.update { it.copy(submissionState = SubmissionState.Success) }
                    } else {
                        Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                        _uiState.update { it.copy(submissionState = SubmissionState.Error(UNEXPECTED_CREDENTIAL)) }
                    }
                }
            }
            catch (_: TimeoutCancellationException) {
                _uiState.update { it.copy(submissionState = SubmissionState.Error(TIMEOUT_ERROR)) }
            }
            catch (_: Exception) {
                _uiState.update { it.copy(submissionState = SubmissionState.Error(DEFAULT_ERROR)) }
            }
        }
    }

    init {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
        }
    }

    fun onEvent (event: AuthLoginEvent) {
        when (event) {
            AuthLoginEvent.NavigateBack -> navigator.navigate(Routes.AuthMain)
            AuthLoginEvent.NavigateToRegister -> navigator.navigate(Routes.AuthRegister)
            AuthLoginEvent.NavigateToDashboard -> navigator.navigate(Routes.DashboardMain)
            AuthLoginEvent.Login -> onLogInClick()
            is AuthLoginEvent.GoogleLogIn -> onGoogleLoginClick(event.credential)
            is AuthLoginEvent.UpdateEmail -> updateEmailField(event.newEmail)
            is AuthLoginEvent.UpdatePassword -> updatePasswordField(event.newPassword)
        }
    }
}