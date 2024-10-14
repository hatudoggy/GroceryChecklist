package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar() {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var localDensity = LocalDensity.current
    var columnHeightDp by remember { mutableStateOf(0.dp) }

    Row(Modifier
        .clip(shape = RoundedCornerShape(20.dp))
        .border(1.dp, Color.Gray.copy(0.5f), RoundedCornerShape(20.dp))
        .padding(15.dp, 10.dp)
        .fillMaxWidth()
        .onGloballyPositioned { coordinates ->
            columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
        }
        .heightIn(min = columnHeightDp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Search, "menu", modifier = Modifier
                .heightIn(max = columnHeightDp), tint = Color.Gray
        )
        BasicTextField(
            value = query,
            onValueChange = { newQuery: TextFieldValue ->
                query = newQuery
            },
            maxLines = 1,
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        if (query.text.isEmpty()) {
                            Text("Search", fontSize = 14.sp, color = Color.Gray.copy(0.7f))
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarComponentPreview() {
    SearchBar()
}