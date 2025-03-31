package com.example.grocerychecklist.viewmodel.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.util.NetworkUtils
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsMainViewModel(
    private val navigator: Navigator,
    private val accountService: AccountService,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsMainState())
    val state: StateFlow<SettingsMainState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), SettingsMainState()
    )

    init {
        updateUser()
    }

    private fun updateUser(){
        if (accountService.hasUser()){
            val user = accountService.getUserProfile()
            _state.update {
                it.copy(
                    userName = user.displayName.ifBlank { "Guest" },
                    userEmail = user.email.ifBlank { "Not Logged In" },
                    isLoggedIn = user.isAnonymous.not()
                )
            }
        } else {
            _state.update {
                it.copy(
                    userName = "Guest",
                    userEmail = "Not Logged In",
                    isLoggedIn = false
                )
            }
        }
    }

    private fun signOut(){
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            try {
                // Show sign-out indicator
                _state.update { it.copy(isSigningOut = true) }

                // Give UI time to show the overlay
                delay(100)

                // Perform the sign-out operation
                accountService.signOut()

                // Keep the indicator visible for a moment to ensure user sees feedback
                delay(800)

                // Navigate to login screen
                navigator.navigate(Routes.AuthLogin)
            } finally {
                // Ensure the signing out state is reset
                _state.update { it.copy(isSigningOut = false) }
            }
        }
    }

    private fun resetPassword() {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            _state.update {it.copy(isPasswordReset = false, error = null)}

            try {
                accountService.resetPassword()
                _state.update { it.copy(isPasswordReset = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private var pendingEmail: String? = null

    private fun updateEmail(newEmail: String) {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            try {
                accountService.updateEmail(newEmail)
                _state.update {
                    it.copy(
                        isEmailUpdateSent = true,
                        pendingEmail = null,
                        showReauthDialog = false
                    )
                }
            } catch (e: Exception) {
                if (e is FirebaseAuthRecentLoginRequiredException) {
                    pendingEmail = newEmail
                    _state.update { it.copy(showReauthDialog = true) }
                } else {
                    _state.update { it.copy(error = e.message) }
                }
            }
        }
    }

    private fun reauthenticate(password: String) {
        viewModelScope.launch {
            try {
                accountService.reauthenticate(password)

                pendingEmail?.let { email ->
                    accountService.updateEmail(email)
                    _state.update {
                        it.copy(
                            isEmailUpdateSent = true,
                            pendingEmail = null,
                            showReauthDialog = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun onEvent(event: SettingsMainEvent) {
        when (event) {
            SettingsMainEvent.ToggleBottomModal -> {
                _state.update { it.copy(isBottomModalOpen = !it.isBottomModalOpen) }
            }
            SettingsMainEvent.LogIn -> {
                navigator.navigate(Routes.AuthLogin)
            }
            SettingsMainEvent.SignOut -> {
                signOut()
            }
            SettingsMainEvent.ResetPassword -> {
                resetPassword()
            }

            SettingsMainEvent.ClearResetState -> {
                _state.update { it.copy(isPasswordReset =  false) }
            }

            SettingsMainEvent.ClearErrorState -> {
                _state.update { it.copy(error = null) }
            }

            SettingsMainEvent.ClearEmailUpdateState -> {
                _state.update { it.copy(isEmailUpdateSent = false) }
            }

            SettingsMainEvent.ClearPendingEmail -> {
                pendingEmail= null
                _state.update { it.copy(showReauthDialog = false) }
            }

            is SettingsMainEvent.UpdateEmail -> updateEmail(event.newEmail)

            is SettingsMainEvent.PromptReauthentication -> {
                pendingEmail = event.newEmail
                _state.update { it.copy(showReauthDialog = true) }
            }

            is SettingsMainEvent.Reauthenticate -> reauthenticate(event.password)



        }
    }
}