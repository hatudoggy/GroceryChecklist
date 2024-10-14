package com.example.grocerychecklist.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.component.TopBarComponent

@Composable
fun SettingsMainScreen() {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "Settings") },
    ) {
            innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsMainScreenPreview() {
    SettingsMainScreen()
}