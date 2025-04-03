package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.grocerychecklist.ui.screen.checklist.ChecklistStartScreen
import com.example.grocerychecklist.ui.theme.ErrorFilled
import com.example.grocerychecklist.ui.theme.ErrorText
import com.example.grocerychecklist.ui.theme.ErrorTonal


@Composable
fun AlertDialogExtend(
    isOpen: Boolean,
    onClose: () -> Unit,
    title: String,
    body: String,
    actionButtons: @Composable () -> Unit
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = { onClose() }
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardColors(
                    containerColor = Color.White,
                    contentColor = CardDefaults.cardColors().contentColor,
                    disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
                    disabledContentColor = CardDefaults.cardColors().disabledContentColor,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    Column(
                        Modifier
                            .fillMaxWidth()
                            //.defaultMinSize(minHeight = 120.dp)
                    ) {
                        Text(
                            title,
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(body)
                    }
                    Spacer(Modifier.height(28.dp))
                    actionButtons()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AlertDialogExtendPreview() {
    AlertDialogExtend(
        isOpen = true,
        onClose = {},
        title = "Delete",
        body = "Are you sure?",
        actionButtons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalButton(
                    onClick = {},
                    colors = ButtonColors(
                        containerColor = ErrorTonal,
                        contentColor = ErrorText,
                        disabledContainerColor = ErrorTonal,
                        disabledContentColor = ErrorText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete Checklist Item")
                }
                FilledTonalButton(
                    onClick = {},
                    colors = ButtonColors(
                        containerColor = ErrorTonal,
                        contentColor = ErrorText,
                        disabledContainerColor = ErrorTonal,
                        disabledContentColor = ErrorText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete Checklist Item & Item")
                }
                TextButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        }
    )
}