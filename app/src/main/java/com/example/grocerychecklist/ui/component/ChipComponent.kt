package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.theme.LightGray

@Composable
fun ChipComponent(
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .background(
                if (isActive)
                    MaterialTheme.colorScheme.primary else
                    LightGray
            )
            .padding(horizontal = 14.dp, vertical = 4.dp)

    ) {
        Text(
            label,
            color = if (isActive)
                Color.White else Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipComponentPreview() {
    ChipComponent(
        label = "Checked"
    )
}

@Preview(showBackground = true)
@Composable
fun ChipComponentActivePreview() {
    ChipComponent(
        label = "Checked",
        isActive = true
    )
}