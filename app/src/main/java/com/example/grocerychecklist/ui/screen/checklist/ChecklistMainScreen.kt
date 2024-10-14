package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.SearchBar
import com.example.grocerychecklist.ui.theme.Maroon
import com.example.grocerychecklist.ui.theme.SkyGreen

@Composable
fun ChecklistMainScreen() {
    Scaffold(modifier = Modifier.padding(10.dp)) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SearchBar()
            Column {
                ChecklistCard(
                    Icons.Default.Fastfood,
                    SkyGreen,
                    "Main Grocery",
                    "Aug 10, 2023",
                    "A checklist for the main groceries for the month. All the essentials..."
                )
                ChecklistCard(
                    Icons.Default.Medication,
                    Maroon,
                    "Grandpa's Meds",
                    "Aug 10, 2023",
                    "Important to buy it weekly."
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistMainScreenPreview() {
    ChecklistMainScreen()
}

@Composable
fun ChecklistCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    date: String,
    description: String
) {
    Row(
        modifier = Modifier
            .heightIn(min = 60.dp, max = 60.dp)
            .clickable(onClick = {
                println("Hello")
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp, 0.dp)
        ) {
            Icon(
                icon, "menu", modifier = Modifier
                    .size(42.5.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(color = iconBackgroundColor)
                    .padding(7.dp)
                    .align(Alignment.CenterVertically), tint = Color.White
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(title, fontWeight = FontWeight(600))
                    Text(
                        date,
                        fontSize = 12.sp,
                        color = Color.Gray.copy(0.8f)
                    )
                }
                Text(
                    description,
                    fontSize = 12.sp,
                    color = Color.Gray.copy(0.8f),
                    // This fixes the additional line padding when text is wrapped
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth(0.78f)
                )
            }
        }
    }
}