package com.example.grocerychecklist.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.GoogleButton
import com.example.grocerychecklist.ui.component.SuccessAnimationComponent
import com.example.grocerychecklist.ui.component.ToastComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.component.launchCredManBottomSheet
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.viewmodel.auth.AuthLoginEvent
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterEvent
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterState
import com.example.grocerychecklist.viewmodel.util.SubmissionState
import kotlinx.coroutines.delay


@Composable
fun AuthRegisterScreen (
    state: AuthRegisterState,
    onEvent: (AuthRegisterEvent) -> Unit
) {
    val context = LocalContext.current

    // Track the latest error message to show
    var errorMessage by rememberSaveable { mutableStateOf("") }

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Show error message when submission state changes to Error
    LaunchedEffect(state.submissionState) {
        when (state.submissionState) {
            is SubmissionState.Error -> {
                errorMessage = state.submissionState.message
            }
            is SubmissionState.Success -> {
                delay(2500)
                onEvent(AuthRegisterEvent.NavigateToDashboard)
            }
            else -> {}
        }
    }

    LaunchedEffect(key1 = "google_sign_up") {
        launchCredManBottomSheet(context) { result ->
            onEvent(AuthRegisterEvent.GoogleSignUp(result))
        }
    }

    Scaffold(
        topBar = {
            TopBarComponent(
                "",
                onNavigateBackClick = { onEvent(AuthRegisterEvent.NavigateBack) }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 0.dp, bottom = 24.dp)
        ) {
            when(state.submissionState) {
                is SubmissionState.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp)
                    ) {
                        SuccessAnimationComponent()
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Registration Successful!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Welcome to the app...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                else -> {
                    Column (modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Create a new account",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryGreen,
                        )

                        Spacer(Modifier.height(8.dp))
//                Text(
//                    "Register to get started",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
                    }

                    Spacer(Modifier.height(16.dp))

                    //Form section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val textFieldModifier = Modifier
                            .fillMaxWidth()

                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = { onEvent(AuthRegisterEvent.FullNameChanged(it)) },
                            label = { Text("Full Name") },
                            modifier = textFieldModifier,
                            singleLine = true,
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            )
                        )
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { onEvent(AuthRegisterEvent.EmailChanged(it)) },
                            label = { Text("Email") },
                            modifier = textFieldModifier,
                            singleLine = true,
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            isError = state.emailError != null,
                        )
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(AuthRegisterEvent.PasswordChanged(it)) },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = "Password Icon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            isError = state.passwordError != null,
                            trailingIcon = {
                                val image = if (isPasswordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                val description = if (isPasswordVisible) "Hide password" else "Show password"

                                IconButton(onClick = {!isPasswordVisible}) {
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            },
                            modifier = textFieldModifier
                        )
                        OutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = { onEvent(AuthRegisterEvent.ConfirmPasswordChanged(it)) },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Security, contentDescription = "Password Icon") },
                            modifier = textFieldModifier,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            isError = state.confirmPasswordError != null,
                        )
                    }

                    // Error texts
                    Column {
                        val errors = listOf(
                            state.emailError,
                            state.passwordError,
                            state.confirmPasswordError
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        errors.forEach { error ->
                            AnimatedVisibility(
                                visible = error != null,
                                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 300))
                            ) {
                                error?.let {
                                    Text(text = "- $it", color = Color.Red, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Flexible space to push buttons to bottom
                    Spacer(modifier = Modifier.weight(1f))

                    // Buttons section with proper bottom padding
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SignUpButton(onEvent, state.isFormValid, state.submissionState is SubmissionState.Loading)
                        SocialSignUpDivider()
                        GoogleSignUpButton(onEvent, state.submissionState is SubmissionState.Loading)
                        LoginPrompt(onEvent)
                    }
                }
            }
        }
    }
    ToastComponent(message = errorMessage, onDismiss = { errorMessage = "" })
}

@Composable
private fun SignUpButton(onEvent: (AuthRegisterEvent) -> Unit, isFormValid: Boolean, isLoading: Boolean) {
    Button(
        onClick = {
            if (isFormValid && !isLoading) {
                onEvent(AuthRegisterEvent.EmailSignUp)
            }
        },
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth(),
        enabled = isFormValid && !isLoading
    ) {
        if (isLoading){
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text("Sign Up")
        }
    }
}

@Composable
private fun SocialSignUpDivider(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Text(
            "Or sign up with",
            Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(Modifier.weight(1f))
    }
}

@Composable
fun GoogleSignUpButton(onEvent: (AuthRegisterEvent) -> Unit, isLoading: Boolean){
    GoogleButton(
        progressIndicatorColor = PrimaryGreen,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        isLoading = isLoading
    ){ credential ->
        onEvent(AuthRegisterEvent.GoogleSignUp(credential))
    }
}

@Composable
private fun LoginPrompt(onEvent: (AuthRegisterEvent) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = 28.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Already Signed Up?",
        )

        Text(
            text="Login now",
            color = Color(0xFF2565BE),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                onEvent(AuthRegisterEvent.NavigateToLogin)
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AuthRegisterScreenPrev() {
    AuthRegisterScreen(
        state = AuthRegisterState(),
        onEvent = {}
    )
}