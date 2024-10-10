package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardBreakdownScreen() {
    Scaffold {
            innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardBreakdownPreview() {
    DashboardBreakdownScreen()
}