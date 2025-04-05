package com.example.grocerychecklist.ui.screen.checklist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.data.mapper.ChecklistInput
import com.example.grocerychecklist.ui.component.ButtonCardIconComponent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistStartState

@Composable
fun CopyChecklistDialogComponent(
    state: ChecklistStartState,
    onDismiss: () -> Unit,
    onConfirm: (ChecklistInput) -> Unit,
    onSetNewChecklist: (ChecklistInput) -> Unit,
    toggleIconPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(
                start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
            )
    ) {
        Text(
            ("Copying ${state.selectedItems.size} items to a new Checklist"),
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { toggleIconPicker() }
            ) {
                ButtonCardIconComponent(
                    backgroundColor = state.newChecklist.iconBackgroundColor.color,
                    icon = state.newChecklist.icon.imageVector,
                    wrapperSize = 56.dp,
                    iconSize = 30.dp
                )
            }

            // Name of Checklist
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.newChecklist.name,
                onValueChange = { e -> onSetNewChecklist(state.newChecklist.copy(name = e))
                },
                label = { Text("Name") }
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") },
            minLines = 5,
            value = state.newChecklist.description,
            onValueChange = { e ->
                onSetNewChecklist(state.newChecklist.copy(description = e))
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row {
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.padding(end = 8.dp)
                ) { Text("Cancel") }

                Button(
                    enabled = state.newChecklist.name.trim().isNotEmpty(),
                    onClick = { onConfirm(state.newChecklist) },
                ) {
                    Text("Save and Copy")
                }
            }
        }
    }
}
