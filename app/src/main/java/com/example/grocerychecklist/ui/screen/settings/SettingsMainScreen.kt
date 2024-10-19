package com.example.grocerychecklist.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.BottomModalComponent
import com.example.grocerychecklist.ui.component.BottomModalButtonComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen
import java.util.Locale

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
                .fillMaxSize()
        ) {
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