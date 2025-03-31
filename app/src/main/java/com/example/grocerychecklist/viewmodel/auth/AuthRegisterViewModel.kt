import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.util.UNEXPECTED_CREDENTIAL
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.util.DEFAULT_ERROR
import com.example.grocerychecklist.util.NetworkUtils
import com.example.grocerychecklist.util.TIMEOUT_ERROR
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterEvent
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterState
import com.example.grocerychecklist.viewmodel.util.MIN_PASS_LENGTH
import com.example.grocerychecklist.viewmodel.util.PASS_PATTERN
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.regex.Pattern
import kotlinx.coroutines.withTimeout

private const val ERROR_TAG = "AuthRegisterViewModel"


// Data class to hold the form state
class AuthRegisterViewModel(
    private val navigator: Navigator,
    private val accountService: AccountService,
    private val application: Application
) : ViewModel() {

    // Use a single state flow to manage the entire state
    private val _state = MutableStateFlow(AuthRegisterState())
    val state: StateFlow<AuthRegisterState> = _state.asStateFlow()

    // Update the full name
    private fun updateFullName(newName: String) {
        _state.update { currentState ->
            currentState.copy(
                fullName = newName,
                error = null
            )
        }
    }
    // Update the email and validate it
    private fun updateEmail(newEmail: String) {
        _state.update { currentState ->
            val isValid = newEmail.isValidEmail()
            currentState.copy(
                email = newEmail,
                isEmailValid = isValid,
                emailError = if (isValid) null else if (newEmail.isBlank()) "Email cannot be empty" else "Invalid email address",
                error = null
            ).validateForm()
        }
    }

    // Update the password and validate it
    private fun updatePassword(newPassword: String) {
        _state.update { currentState ->
            val passwordError = when {
                newPassword.isBlank() -> "Password cannot be empty"
                newPassword.length < MIN_PASS_LENGTH -> "Password must be at least $MIN_PASS_LENGTH characters"
                else -> {
                    val missingRequirements = mutableListOf<String>()
                    if (!newPassword.matches(Regex(".*\\d.*"))) {
                        missingRequirements.add("number")
                    }
                    if (!newPassword.matches(Regex(".*[a-z].*"))) {
                        missingRequirements.add("lowercase")
                    }
                    if (!newPassword.matches(Regex(".*[A-Z].*"))) {
                        missingRequirements.add("uppercase")
                    }
                    if (!newPassword.matches(Regex(".*[^a-zA-Z\\d\\s].*"))) {
                        missingRequirements.add("special character")
                    }

                    if (missingRequirements.isNotEmpty()) {
                        "Missing: ${missingRequirements.joinToString(", ")}"
                    } else {
                        null
                    }
                }
            }

            currentState.copy(
                password = newPassword,
                isPasswordValid = passwordError == null,
                passwordError = passwordError,
                error = null
            ).validateForm()
        }
    }

    // Update the confirm password and validate it
    private fun updateConfirmPassword(newConfirmPassword: String) {
        _state.update { currentState ->
            val isValid = newConfirmPassword == currentState.password
            currentState.copy(
                confirmPassword = newConfirmPassword,
                isConfirmPasswordValid = isValid,
                confirmPasswordError = if (isValid) null else "Passwords do not match",
                error = null
            ).validateForm()
        }
    }

    // Extension functions for validation
    private fun String.isValidEmail(): Boolean {
        return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        return this.isNotBlank() &&
                this.length >= MIN_PASS_LENGTH &&
                Pattern.compile(PASS_PATTERN).matcher(this).matches()
    }

    // Function to validate the entire form
    private fun AuthRegisterState.validateForm(): AuthRegisterState {
        return this.copy(isFormValid = isEmailValid && isPasswordValid && isConfirmPasswordValid)
    }

    private fun updateDisplayNameOnSignUp() {
        viewModelScope.launch {
            val fullName = _state.value.fullName
            if (fullName.isNotBlank()) {
                try {
                    withTimeout(5000) {
                        accountService.updateDisplayName(fullName)
                    }
                } catch (e: SocketTimeoutException) {
                    _state.update { it.copy(error = TIMEOUT_ERROR) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = DEFAULT_ERROR) }
                }
            }
        }
    }

    private fun onSignUpClick() {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                withTimeout(5000) {
                    val currentState = _state.value
                    if (!currentState.isFormValid) {
                        throw IllegalArgumentException("Invalid form data")
                    }
                    accountService.linkAccountWithEmail(currentState.email, currentState.password)
                    updateDisplayNameOnSignUp()
                    navigator.navigate(Routes.DashboardMain)
                }
            } catch (e: SocketTimeoutException) {
                _state.update { it.copy(error = TIMEOUT_ERROR, isLoading = false) }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = DEFAULT_ERROR, isLoading = false) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onSignUpWithGoogle(credential: Credential) {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                withTimeout(5000) {
                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                        navigator.navigate(Routes.DashboardMain)
                    } else {
                        Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                        _state.update { it.copy(error = UNEXPECTED_CREDENTIAL) }
                    }
                }
            } catch (e: SocketTimeoutException) {
                _state.update { it.copy(error = TIMEOUT_ERROR, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: DEFAULT_ERROR, isLoading = false) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    init {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(error = "No internet connection") }
        }
    }

    fun onEvent (event: AuthRegisterEvent) {
        when (event) {
            AuthRegisterEvent.NavigateBack -> navigator.popBackStack()
            AuthRegisterEvent.NavigateToLogin -> navigator.navigate(Routes.AuthLogin)
            AuthRegisterEvent.EmailSignUp -> onSignUpClick()

            is AuthRegisterEvent.GoogleSignUp -> onSignUpWithGoogle(event.credential)
            is AuthRegisterEvent.FullNameChanged -> updateFullName(event.newName)
            is AuthRegisterEvent.EmailChanged -> updateEmail(event.newEmail)
            is AuthRegisterEvent.PasswordChanged -> updatePassword(event.newPassword)
            is AuthRegisterEvent.ConfirmPasswordChanged -> updateConfirmPassword(event.newConfirmPassword)
            AuthRegisterEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
        }
    }
}

