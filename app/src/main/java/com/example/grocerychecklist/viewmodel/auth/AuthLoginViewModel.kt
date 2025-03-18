package com.example.grocerychecklist.viewmodel.auth

import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.core.copy
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.util.ERROR_TAG
import com.example.grocerychecklist.util.UNEXPECTED_CREDENTIAL
import com.example.grocerychecklist.data.model.service.AccountService
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.util.DEFAULT_ERROR
import com.example.grocerychecklist.util.NetworkUtils
import com.example.grocerychecklist.util.TIMEOUT_ERROR
import com.example.grocerychecklist.viewmodel.util.MIN_PASS_LENGTH
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

    private fun updateEmail(newEmail: String) {
        _uiState.update { currentState ->
            val isValid = newEmail.isValidEmail()
            currentState.copy(
                email = newEmail,
                isEmailValid = isValid,
                emailError = if (!isValid) "Invalid email address" else null,
                error = null
            ).validateForm()
        }
    }

    private fun updatePassword(newPassword: String) {
        _uiState.update { currentState ->
            val isValid = newPassword.isValidPassword()
            currentState.copy(
                password = newPassword,
                isPasswordValid = isValid,
                passwordError = if (!isValid) "Invalid password" else null,
                error = null
            ).validateForm()
        }
    }

    private fun AuthLoginState.validateForm(): AuthLoginState {
        return this.copy(isFormValid = isEmailValid && isPasswordValid)
    }

    private fun String.isValidEmail(): Boolean {
        return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        return this.isNotBlank() && this.length >= MIN_PASS_LENGTH && this.matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,}$"))
    }

    private fun onLogInClick() {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try{
                withTimeout(5000){
                    accountService.signInWithEmail(_uiState.value.email, _uiState.value.password)
                    navigator.navigate(Routes.DashboardMain)
                }
            }
            catch (e: TimeoutCancellationException) {
                _uiState.update { it.copy(error = TIMEOUT_ERROR, isLoading = false) }
            }
            catch (e: Exception) {
                _uiState.update { it.copy(error = "Invalid email or password", isLoading = false) }
            }finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onGoogleLoginClick(credential: Credential) {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                withTimeout(5000) {
                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                        navigator.navigate(Routes.DashboardMain)
                    } else {
                        Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                        _uiState.update { it.copy(error = UNEXPECTED_CREDENTIAL, isLoading = false) }
                    }
                }
            }
            catch (e: TimeoutCancellationException) {
                _uiState.update { it.copy(error = TIMEOUT_ERROR, isLoading = false) }
            }
            catch (e: Exception) {
                _uiState.update { it.copy(error = DEFAULT_ERROR, isLoading = false) }
            }finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    init {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _uiState.update { it.copy(error = "No internet connection") }
        }
    }

    fun onEvent (event: AuthLoginEvent) {
        when (event) {
            AuthLoginEvent.NavigateBack -> navigator.navigate(Routes.AuthMain)
            AuthLoginEvent.Login -> onLogInClick()
            is AuthLoginEvent.GoogleLogIn -> onGoogleLoginClick(event.credential)
            is AuthLoginEvent.UpdateEmail -> updateEmail(event.newEmail)
            is AuthLoginEvent.UpdatePassword -> updatePassword(event.newPassword)
        }
    }
}