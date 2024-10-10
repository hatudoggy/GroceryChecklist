package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardMainScreen() {
    Scaffold {
        innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "Dashboard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardMainScreenPreview() {
    DashboardMainScreen()
}