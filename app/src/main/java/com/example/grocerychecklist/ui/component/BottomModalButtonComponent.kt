package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen

@Composable
fun BottomModalButtonComponent(
    onClick: () -> Unit = {},
    title: String,
    subTitle: String? = null
) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(8.dp))
//            .clickable(onClick = onClick)
//            .padding(horizontal = 16.dp, vertical = 16.dp)
//
//    ) {
//        Text(
//            text,
//            style = TextStyle(
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Normal,
//                lineHeight = 28.sp,
//                letterSpacing = 1.sp
//            ),
//        )
//    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        Column(
            verticalArrangement = if (subTitle != null) Arrangement.spacedBy(2.dp) else Arrangement.Center,
            modifier = Modifier.heightIn(min = 35.dp),
        ) {
            Text(
                title, style = TextStyle(
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
            )
            if (subTitle != null)
                Text(
                    subTitle, style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    ), color = Color.Gray
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomModalButtonComponentPreview() {
    BottomModalButtonComponent(title = "Philippine Peso", subTitle = "P")
}