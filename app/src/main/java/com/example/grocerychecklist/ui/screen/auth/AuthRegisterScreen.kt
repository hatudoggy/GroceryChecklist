package com.example.grocerychecklist.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.GoogleButton
import com.example.grocerychecklist.ui.component.ToastComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.component.launchCredManBottomSheet
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterEvent
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterState


@Composable
fun AuthRegisterScreen (
    state: AuthRegisterState,
    onEvent: (AuthRegisterEvent) -> Unit
) {
    val context = LocalContext.current

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
            Column (modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Create a",
                    fontSize = 36 .sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen,
                )
                Text(
                    "new account",
                    fontSize = 36.sp,
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
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onEvent(AuthRegisterEvent.EmailChanged(it)) },
                    label = { Text("Email") },
                    modifier = textFieldModifier,
                    singleLine = true,
                    isError = state.emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(AuthRegisterEvent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = state.passwordError != null,
                    leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = "Password Icon") },
                    trailingIcon = {
                        val image = if (state.isPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (state.isPasswordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { onEvent(AuthRegisterEvent.TogglePasswordVisibility) }) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = state.confirmPasswordError != null,
                    leadingIcon = { Icon(imageVector = Icons.Default.Security, contentDescription = "Password Icon") },
                    modifier = textFieldModifier
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
                SignUpButton(onEvent, state)
                SocialSignUpDivider()
                GoogleSignUpButton(onEvent)
            }
        }
        ToastComponent(message = state.error)
    }
}

@Composable
private fun SignUpButton(onEvent: (AuthRegisterEvent) -> Unit, state: AuthRegisterState) {
    Button(
        onClick = {
            onEvent(AuthRegisterEvent.EmailSignUp)
        },
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth(),
        enabled = state.isEmailValid &&
                state.isPasswordValid &&
                state.isConfirmPasswordValid
    ) {
        Text("Register", fontSize = 16.sp)
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
fun GoogleSignUpButton(onEvent: (AuthRegisterEvent) -> Unit){
    GoogleButton(
        text = "Sign up with Google",
        loadingText = "Signing Up",
        progressIndicatorColor = PrimaryGreen,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ){ credential ->
        onEvent(AuthRegisterEvent.GoogleSignUp(credential))
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