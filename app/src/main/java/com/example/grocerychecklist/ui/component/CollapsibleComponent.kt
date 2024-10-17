package com.example.grocerychecklist.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CollapsibleComponent(
    isCardClicked: Boolean,
    cardComponent: @Composable () -> Unit,
    collapsedComponent: @Composable () -> Unit
) {
    cardComponent()
    AnimatedVisibility(
        visible = isCardClicked,
        enter = expandVertically(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(300))
    ) {
        collapsedComponent()
    }
}

@Preview(showBackground = true)
@Composable
fun CollapsibleComponentPreview() {
    CollapsibleComponent(
        isCardClicked = false,
        cardComponent = {
            ButtonCardComponent(
                name = "Main Grocery",
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
                icon = Icons.Filled.Android,
                iconBackgroundColor = MaterialTheme.colorScheme.primary,
                variant = ButtonCardComponentVariant.History,
                onClick = {},
                isClicked = true
            )
        },
        collapsedComponent = {
            Row {
                Spacer(Modifier.fillMaxWidth(0.15f))
                Column {
                    ListItem(headlineContent = {
                        Text("Placeholder", color = Color.Gray)
                    }, trailingContent = {
                        Text("xxx.xx")
                    })
                }
            }
        }
    )
}