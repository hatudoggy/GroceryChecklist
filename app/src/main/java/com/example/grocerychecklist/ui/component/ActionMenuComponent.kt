package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    onEditMenu: () -> Unit,
    onDeleteDialog: () -> Unit
) {
    BottomSheet(
        isOpen = isOpen,
        onClose = {
            onClose()
        }
    ) {
        Column(
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp, 1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onEditMenu()
                    }
                    .padding(10.dp, 15.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                )
                Text(
                    text = "Edit",
                    fontSize = 18.sp,
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp, 1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDeleteDialog() }
                    .padding(10.dp, 15.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
                Text(
                    text = "Delete",
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }
        }
    }
}
