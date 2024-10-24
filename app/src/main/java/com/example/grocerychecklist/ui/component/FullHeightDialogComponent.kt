package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FullHeightDialogComponent(
    onDismissRequest: () -> Unit = {},
    scaffoldTopBar: @Composable () -> Unit,
    scaffoldContent: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Scaffold(contentWindowInsets = WindowInsets(0.dp),
                topBar = {
                    scaffoldTopBar()
                }) { innerPadding ->
                scaffoldContent(innerPadding)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullHeightDialogComponentPreview() {
    FullHeightDialogComponent(scaffoldTopBar = {
        FullHeightDialogMockTopBarComponent({})
    }, scaffoldContent = { innerPadding -> FullHeightDialogMockContentComponent(innerPadding) })
}

@Composable
fun FullHeightDialogMockTopBarComponent(
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(18.dp, 28.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onDismissRequest() }
            )
            {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.04f))
            Text(
                text = "This is a mock component",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
            )
        }
        TextButton(onClick = {}) {
            Text(
                "Save", style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
fun FullHeightDialogMockContentComponent(
    innerPadding: PaddingValues,
) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(18.dp, 0.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = "Name",
            onValueChange = { },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "Description",
            onValueChange = { },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )
    }
}