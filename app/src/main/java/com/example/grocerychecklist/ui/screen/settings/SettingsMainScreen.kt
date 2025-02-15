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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen() {
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
                title = "Guest",
                subTitle = "Not Logged In",
                bottomModalContent = {
                    ColorSchemeBottomModalContentComponent()
                }
            )
            BlurredCardWithLoginPrompt()
        }
    }
}

@Composable
fun BlurredCardWithLoginPrompt() {
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
                .blur(2.dp) // Apply blur effect
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
                    subTitle = "Change profile picture, number, e-mail",
                    bottomModalContent = {
                        ColorSchemeBottomModalContentComponent()
                    }
                )
                SettingsButtonCard(
                    icon = Icons.Default.Edit,
                    title = "Change Password",
                    subTitle = "Update and secure your account",
                    bottomModalContent = {
                        CurrencyBottomModalContentComponent()
                    }
                )
            }
        }

        // Login Button (Above the blurred card)
        Button(
            onClick = { /* Handle login action */ },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp)
                .shadow(5.dp, shape = RoundedCornerShape(50.dp))
        ) {
            Text("Log In to Sync")
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
    val openBottomSheet = rememberSaveable {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState()

    if (openBottomSheet.value) {
        BottomModalComponent(bottomSheetState, {
            openBottomSheet.value = false
        }, content = {
            bottomModalContent()
        }, contentTitle = "Select ${title.lowercase()}")
    }

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
                    .clickable(onClick = {
                        openBottomSheet.value = true
                    })
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
    bottomModalContent: @Composable () -> Unit,
) {
    val openBottomSheet = rememberSaveable {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState()

    if (openBottomSheet.value) {
        BottomModalComponent(bottomSheetState, {
            openBottomSheet.value = false
        }, content = {
            bottomModalContent()
        }, contentTitle = "Select ${title.lowercase()}")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = {
                openBottomSheet.value = true
            })
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
    SettingsMainScreen()
}