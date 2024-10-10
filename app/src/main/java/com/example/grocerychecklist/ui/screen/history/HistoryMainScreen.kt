package com.example.grocerychecklist.ui.screen.history

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryMainScreen() {
    Scaffold(modifier = Modifier.padding(10.dp)) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SearchBar()
            ChecklistCard()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryMainScreenPreview() {
    HistoryMainScreen()
}

@Composable
fun SearchBar() {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    BasicTextField(
        value = query,
        onValueChange = { newQuery: TextFieldValue ->
            query = newQuery
        },
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(10.dp)
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Gray.copy(0.5f), RoundedCornerShape(20.dp))
                        .padding(15.dp, 10.dp)
                ) {
                    if (query.text.isEmpty()) {
                        Text("Search", fontSize = 14.sp, onTextLayout = null, color = Color.Gray.copy(0.7f))
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun ChecklistCard() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(10.dp, 0.dp)
    ) {
        Icon(Icons.Filled.Menu, "menu", modifier = Modifier.size(40.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Main Grocery", onTextLayout = null, fontWeight = FontWeight(600))
                Text(
                    "Aug 10, 2023", onTextLayout = null,
                    fontSize = 12.sp,
                    color = Color.Gray.copy(0.8f)
                )
            }
            Text(
                "A checklist of the main groceries for the month. All the essentials...",
                onTextLayout = null,
                fontSize = 12.sp,
                color = Color.Gray.copy(0.8f),
                modifier = Modifier.fillMaxWidth(0.78f)
            )
        }
    }
}