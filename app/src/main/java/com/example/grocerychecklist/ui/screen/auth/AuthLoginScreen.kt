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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.viewmodel.auth.AuthLoginEvent
import com.example.grocerychecklist.viewmodel.auth.AuthLoginState
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterEvent


@Composable
fun AuthLoginScreen (
    onEvent: (AuthLoginEvent) -> Unit,
    state: AuthLoginState
) {
    Scaffold(
        topBar = {
            TopBarComponent(
                "",
                onNavigateBackClick = { onEvent(AuthLoginEvent.NavigateBack) }
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
                "Login to",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = PrimaryGreen,
            )
            Text(
                "your account",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = PrimaryGreen,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Enter your details below",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = {onEvent(AuthLoginEvent.UpdateEmail(it))},
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                var passwordVisible by rememberSaveable() { mutableStateOf(false) }
                OutlinedTextField(
                    value = state.password,
                    onValueChange = {onEvent(AuthLoginEvent.UpdatePassword(it))},
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            LoginButton(onEvent, state)
            SocialLoginDivider()
            GoogleLoginButton(onEvent)
        }
    }
}

@Composable
private fun LoginButton(onEvent: (AuthLoginEvent) -> Unit, state: AuthLoginState) {
    Button(
        onClick = {
            if (state.email.isNotBlank() && state.password.isNotBlank()) {
                onEvent(AuthLoginEvent.Login)
            }
        },
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth(),
        enabled = state.email.isNotBlank() && state.password.isNotBlank() // Disable button if fields are empty
    ) {
        Text("Login")
    }
}

@Composable
private fun SocialLoginDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 28.dp)
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Text(
            text = "Or login with",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(Modifier.weight(1f))
    }
}

@Composable
private fun GoogleLoginButton(onEvent: (AuthLoginEvent) -> Unit) {
    GoogleButton(
        text = "Login to google",
        loadingText = "Logging In",
        progressIndicatorColor = PrimaryGreen,
        modifier = Modifier.fillMaxWidth(),
    ) { credential ->
        onEvent(AuthLoginEvent.GoogleLogIn(credential))
    }
}

@Preview(showBackground = true)
@Composable
fun AuthLoginScreenPrev() {
    AuthLoginScreen(
        onEvent = {},
        state = AuthLoginState()
    )
}