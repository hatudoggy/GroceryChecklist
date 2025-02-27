package com.example.grocerychecklist.ui.screen.auth

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

    LaunchedEffect(Unit) {
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
                .padding(24.dp)
        ) {
            Text(
                "Create a",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = PrimaryGreen,
            )
            Text(
                "new account",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = PrimaryGreen,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Register to get started",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            //Form section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { onEvent(AuthRegisterEvent.FullNameChanged(it)) },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onEvent(AuthRegisterEvent.EmailChanged(it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = !state.isEmailValid,
                    supportingText = { state.emailError?.let { Text(it, color = Color.Red) } }
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(AuthRegisterEvent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = !state.isPasswordValid,
                    supportingText = { state.passwordError?.let { Text(it, color = Color.Red) } },
                    trailingIcon = {
                        val image = if (state.isPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (state.isPasswordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { onEvent(AuthRegisterEvent.TogglePasswordVisibility) }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { onEvent(AuthRegisterEvent.ConfirmPasswordChanged(it)) },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = !state.isConfirmPasswordValid,
                    supportingText = { state.confirmPasswordError?.let { Text(it, color = Color.Red) } }
                )
            }

            Spacer(Modifier.height(16.dp))

            SignUpButton(onEvent, state)
            SocialSignUpDivider()
            GoogleSignUpButton(onEvent)
        }
    }
}

@Composable
private fun SignUpButton(onEvent: (AuthRegisterEvent) -> Unit, state: AuthRegisterState) {
    Button(
        onClick = {
            onEvent(AuthRegisterEvent.EmailSignUp)
        },
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth(),
        enabled = state.isEmailValid &&
                state.isPasswordValid &&
                state.isConfirmPasswordValid
    ) {
        Text("Register")
    }
}

@Composable
private fun SocialSignUpDivider(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp)
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
        modifier = Modifier.fillMaxWidth(),
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