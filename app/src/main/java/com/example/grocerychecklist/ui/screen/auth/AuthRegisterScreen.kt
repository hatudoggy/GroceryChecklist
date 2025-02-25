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
import com.example.grocerychecklist.viewmodel.auth.AuthRegisterEvent

@Composable
fun AuthRegisterScreen (
    onEvent: (AuthRegisterEvent) -> Unit
) {
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

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                var passwordVisible by rememberSaveable() { mutableStateOf(false) }
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {  },
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth()
            ) { Text("Register") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 28.dp)
            ) {
                HorizontalDivider(Modifier.weight(1f))
                Text(
                    "Or sign up with",
                    Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(Modifier.weight(1f))
            }
            GoogleButton(
                text = "Sign up with Google",
                loadingText = "Signing Up",
                progressIndicatorColor = PrimaryGreen,
                onClicked = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AuthRegisterScreenPrev() {
    AuthRegisterScreen(
        onEvent = {}
    )
}