package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.theme.PrimaryLightGreen

@Composable
fun BottomModalButtonComponent(
    isActive: Boolean = false,
    onClick: () -> Unit = {},
    title: String,
    subTitle: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = if (isActive) PrimaryLightGreen else Color.Unspecified)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = if (subTitle != null) Arrangement.spacedBy(2.dp) else Arrangement.Center,
            modifier = Modifier.heightIn(min = 35.dp),
        ) {
            Text(
                title,
                style = TextStyle(
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                ),
                color = Color.Black,
            )
            if (subTitle != null)
                Text(
                    subTitle, style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    ), color = if (isActive) Color.Black else Color.Gray
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomModalButtonComponentPreview() {
    BottomModalButtonComponent(title = "Philippine Peso", subTitle = "P")
}