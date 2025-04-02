import android.app.Application
import android.util.Log
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
import com.example.grocerychecklist.viewmodel.util.AuthFormValidator
import com.example.grocerychecklist.viewmodel.util.SubmissionState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
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

    init {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
        }
    }

    // Update the full name
    private fun updateFullNameField(newName: String) {
        _state.update { currentState ->
            currentState.copy(
                fullName = newName,
            )
        }
    }
    // Update the email and validate it
    private fun updateEmailField(newEmail: String) {
        val emailValidation = AuthFormValidator.validateEmail(newEmail)
        _state.update { currentState ->
            currentState.copy(
                email = newEmail,
                emailError = emailValidation.error,
            )
        }
    }

    // Update the password and validate it
    private fun updatePasswordField(newPassword: String) {
        val passwordValidation = AuthFormValidator.validatePassword(newPassword)
        _state.update { currentState ->
            currentState.copy(
                password = newPassword,
                passwordError = passwordValidation.error,
            )
        }
    }

    // Update the confirm password and validate it
    private fun updateConfirmPassword(newConfirmPassword: String) {
        val confirmPasswordValidation = AuthFormValidator.validateConfirmPassword(_state.value.password, newConfirmPassword)
        _state.update { currentState ->
            currentState.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = confirmPasswordValidation.error,
            )
        }
    }

    private fun updateDisplayNameOnSignUp() {
        viewModelScope.launch {
            val fullName = _state.value.fullName
            if (fullName.isNotBlank()) {
                try {
                    withTimeout(5000) {
                        accountService.updateDisplayName(fullName)
                    }
                } catch (_: SocketTimeoutException) {
                    _state.update { it.copy(submissionState = SubmissionState.Error(TIMEOUT_ERROR)) }
                } catch (_: Exception) {
                    _state.update { it.copy(submissionState = SubmissionState.Error(DEFAULT_ERROR)) }
                }
            }
        }
    }

    private fun onSignUpClick() {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(submissionState = SubmissionState.Loading)}
                withTimeout(5000) {
                    val currentState = _state.value
                    if (!currentState.isFormValid) {
                        throw IllegalArgumentException("Invalid form data")
                    }
                    accountService.linkAccountWithEmail(currentState.email, currentState.password)
                    updateDisplayNameOnSignUp()
                    _state.update { it.copy(submissionState = SubmissionState.Success) }
                }
            } catch (_: SocketTimeoutException) {
                _state.update { it.copy(submissionState = SubmissionState.Error(TIMEOUT_ERROR)) }
            } catch (_: Exception) {
                _state.update { it.copy(submissionState = SubmissionState.Error(DEFAULT_ERROR)) }
            }
        }
    }

    private fun onSignUpWithGoogle(credential: Credential) {
        if (!NetworkUtils.isInternetAvailable(application)) {
            _state.update { it.copy(submissionState = SubmissionState.Error("No internet connection")) }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(submissionState = SubmissionState.Loading)}
                withTimeout(5000) {
                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                        _state.update { it.copy(submissionState = SubmissionState.Success) }
                    } else {
                        Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                        _state.update { it.copy(submissionState = SubmissionState.Error(UNEXPECTED_CREDENTIAL)) }
                    }
                }
            } catch (_: SocketTimeoutException) {
                _state.update { it.copy(submissionState = SubmissionState.Error(TIMEOUT_ERROR)) }
            } catch (_: Exception) {
                _state.update { it.copy(submissionState = SubmissionState.Error(DEFAULT_ERROR)) }
            }
        }
    }

    fun onEvent (event: AuthRegisterEvent) {
        when (event) {
            AuthRegisterEvent.NavigateBack -> navigator.popBackStack()
            AuthRegisterEvent.NavigateToLogin -> navigator.navigate(Routes.AuthLogin)
            AuthRegisterEvent.NavigateToDashboard -> navigator.navigate(Routes.DashboardMain)
            AuthRegisterEvent.EmailSignUp -> onSignUpClick()

            is AuthRegisterEvent.GoogleSignUp -> onSignUpWithGoogle(event.credential)
            is AuthRegisterEvent.FullNameChanged -> updateFullNameField(event.newName)
            is AuthRegisterEvent.EmailChanged -> updateEmailField(event.newEmail)
            is AuthRegisterEvent.PasswordChanged -> updatePasswordField(event.newPassword)
            is AuthRegisterEvent.ConfirmPasswordChanged -> updateConfirmPassword(event.newConfirmPassword)
        }
    }
}

