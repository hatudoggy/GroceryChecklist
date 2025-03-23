package com.example.grocerychecklist.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.BottomModalButtonComponent
import com.example.grocerychecklist.ui.component.BottomModalComponent
import com.example.grocerychecklist.ui.component.SignOutIndicator
import com.example.grocerychecklist.ui.component.ToastComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen
import com.example.grocerychecklist.viewmodel.settings.SettingsMainEvent
import com.example.grocerychecklist.viewmodel.settings.SettingsMainState

@Composable
fun SettingsMainScreen(
    state: SettingsMainState,
    onEvent: (SettingsMainEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "Settings") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            AvatarButtonCard(
                icon = Icons.Outlined.LightMode,
                title = state.userName,
                subTitle = state.userEmail,
                bottomModalContent = {
                    ColorSchemeBottomModalContentComponent()
                }
            )
            BlurredCardWithLoginPrompt(state, onEvent)
//            PreferencesSection()
        }
    }
    // Sign Out Indicator overlay - will appear on top of the entire screen
    SignOutIndicator(isVisible = state.isSigningOut)
    ToastComponent(message = state.error)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurredCardWithLoginPrompt(
    state: SettingsMainState,
    onEvent: (SettingsMainEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Blurred Card
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(9.dp))
                .blur(if (!state.isLoggedIn) 8.dp else 0.dp) // Apply blur effect
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "Account",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                SettingsButtonCard(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subTitle = if(state.isLoggedIn)"Change profile picture, number, e-mail" else "",
                    state = state,
                )
                SettingsButtonCard(
                    icon = Icons.Default.Edit,
                    title = "Change Password",
                    subTitle = "Update and secure your account",
                    state = state,
                    onClick = { onEvent(SettingsMainEvent.ResetPassword) }
                )
                if (state.isLoggedIn) {
                    SettingsButtonCard(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        title = "Log Out",
                        subTitle = "Sign out from this device",
                        state = state,
                        onClick = { onEvent(SettingsMainEvent.SignOut) }
                    )
                }
            }

        }

        // Login Button (Above the blurred card)
        if(!state.isLoggedIn){
            Button(
                onClick = { onEvent(SettingsMainEvent.LogIn) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(50.dp))
            ) {
                Text("Log In to Sync")
            }
        }

        if (state.isPasswordReset) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsMainEvent.ClearResetState )},
                title = { Text("Password Reset Sent") },
                text = { Text("Check your email for reset instructions.") },
                confirmButton = {
                    TextButton(onClick = { onEvent(SettingsMainEvent.ClearResetState) }) {
                        Text("Ok")
                    }
                }
            )
        }

        if (state.error != null) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsMainEvent.ClearErrorState) },
                title = { Text("Error") },
                text = { Text(state.error) },
                confirmButton = {
                    TextButton ( onClick = {onEvent(SettingsMainEvent.ClearErrorState)} ) {
                        Text("Ok")
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarButtonCard(
    icon: ImageVector,
    title: String,
    subTitle: String,
    bottomModalContent: @Composable () -> Unit,
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 0.dp,
                horizontal = 5.dp
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .padding(10.dp)

            ) {
                Avatar()
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        title, style = TextStyle(
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        subTitle, style = TextStyle(
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        ), color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsButtonCard(
    icon: ImageVector,
    title: String,
    subTitle: String,
    bottomModalContent: @Composable () -> Unit = {},
    state: SettingsMainState = SettingsMainState(),
    onClick: () -> Unit = {},
    onToggleBottomModal: () -> Unit = {},
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val cardModifier = if(state.isLoggedIn){
        Modifier.clickable(onClick = { onClick() })
    }else{
        Modifier
    }
    if (state.isBottomModalOpen) {
        BottomModalComponent(bottomSheetState, {
             onToggleBottomModal()
        }, content = {
            bottomModalContent()
        }, contentTitle = "Select ${title.lowercase()}")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = cardModifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(10.dp)

    ) {
        Icon(
            icon,
            contentDescription = "Light Mode Icon",
            modifier = Modifier.size(28.dp),
            tint = PrimaryDarkGreen
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title, style = TextStyle(
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                subTitle, style = TextStyle(
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                ), color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun Avatar(size: Dp = 48.dp) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(size)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default Avatar",
            tint = Color.White,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun PreferencesSection() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                "Preferences",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                ),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            SettingsButtonCard(
                icon = Icons.Outlined.LightMode,
                title = "Color Scheme",
                subTitle = "Light",
                bottomModalContent = {
                    ColorSchemeBottomModalContentComponent()
                }
            )
            SettingsButtonCard(
                icon = Icons.Outlined.AttachMoney,
                title = "Currency",
                subTitle = "Peso",
                bottomModalContent = {
                    CurrencyBottomModalContentComponent()
                }
            )
        }
    }
}

@Composable
fun ColorSchemeBottomModalContentComponent() {
    BottomModalButtonComponent(onClick = {}, title = "Light", isActive = true)
    BottomModalButtonComponent(onClick = {}, title = "Dark")
}

@Composable
fun CurrencyBottomModalContentComponent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(10) {
            BottomModalButtonComponent(
                title = "Philippine Peso",
                subTitle = "P",
                isActive = it == 0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsMainScreenPreview() {
    val state: SettingsMainState = SettingsMainState(isLoggedIn = true)
    SettingsMainScreen(
        state,
        onEvent = {}
    )
}