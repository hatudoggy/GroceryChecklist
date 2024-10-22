package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocerychecklist.ui.component.FullHeightDialogComponent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel

@Composable
fun ChecklistEditScreen()  {
    val viewModel: ChecklistMainViewModel = viewModel()
    val onDismissRequest = { viewModel.closeDialog() }

    FullHeightDialogComponent(onDismissRequest, scaffoldTopBar = {
        ChecklistDialogTopBarComponent(onDismissRequest)
    }, scaffoldContent = { innerPadding ->
        ChecklistDialogContentComponent(innerPadding)
    })
}

@Composable
fun ChecklistDialogTopBarComponent(
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
                text = "Edit Checklist Item",
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
fun ChecklistDialogContentComponent(
    innerPadding: PaddingValues,
) {
    val viewModel: ChecklistMainViewModel = viewModel()
    val dialogState by viewModel.dialogState.collectAsState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(18.dp, 0.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Product", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = dialogState.checklistName,
            onValueChange = { it -> viewModel.updateChecklistName(it) },
            label = { Text("Tender Juicy Hotdog") },
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Date", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = dialogState.checklistName,
            onValueChange = { it -> viewModel.updateChecklistName(it) },
            label = { Text("10/22/2024") },
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
        ) {
            Text("Price", fontSize = 18.sp)
        }
        OutlinedTextField(
            value = dialogState.checklistName,
            onValueChange = { it -> viewModel.updateChecklistName(it) },
            label = { Text("250") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistEditScreenPreview() {
    ChecklistEditScreen()
}
