package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.grocerychecklist.ui.screen.Routes


data class MainRoute (
    val route: Routes,
    val icon: ImageVector
)

@Composable
fun BottomBarComponent(
    onNavigateClick: (route: Routes) -> Unit = {},
) {
    val mainRoutes = listOf(
            MainRoute(Routes.DashboardMain, Icons.Filled.Dashboard),
            MainRoute(Routes.ChecklistMain, Icons.Filled.Checklist),
            MainRoute(Routes.ItemMain, Icons.Filled.Fastfood),
            MainRoute(Routes.HistoryMain, Icons.Filled.History),
            MainRoute(Routes.SettingsMain, Icons.Filled.Settings),
        )

    BottomAppBar(
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                mainRoutes.forEach { route ->
                    IconButton(onClick = { onNavigateClick(route.route) }) {
                        Icon(route.icon, contentDescription = "Description")
                    }
                }
            }
        },

    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarComponentPreview() {
    BottomBarComponent()
}