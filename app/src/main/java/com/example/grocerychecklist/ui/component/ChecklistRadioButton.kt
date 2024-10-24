package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChecklistRadioButton(
    selected: Boolean = false
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .border(
                2.dp,
                if (selected) Color.Transparent else Color.LightGray,
                CircleShape
            )
            .size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Filled.Check,
                "Check",
                Modifier.size(16.dp),
                Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistRadioButtonPreview() {
    ChecklistRadioButton()
}

@Preview(showBackground = true)
@Composable
fun ChecklistRadioSelectedButtonPreview() {
    ChecklistRadioButton(selected = true)
}