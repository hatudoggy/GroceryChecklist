package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ChecklistMainScreen() {
    Scaffold {
            innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "Checklist")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreview() {
    ChecklistMainScreen()
}