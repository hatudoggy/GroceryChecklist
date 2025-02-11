package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grocerychecklist.ui.screen.Routes


data class MainRoute(
    val route: Routes,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

@Composable
fun BottomBarComponent(
    activeButton: String? = null,
    onNavigateClick: (route: Routes) -> Unit = {},
) {
    val mainRoutes = listOf(
        MainRoute(Routes.DashboardMain, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        MainRoute(Routes.ItemMain, Icons.Filled.Book, Icons.Outlined.Book),
        MainRoute(Routes.ChecklistMain, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        MainRoute(Routes.HistoryMain, Icons.Filled.History, Icons.Outlined.History),
        MainRoute(Routes.SettingsMain, Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        BottomAppBar(
            containerColor = Color.White,
            modifier = Modifier
                .height(70.dp)
                .shadow(24.dp),
            actions = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    mainRoutes.forEach { route ->
                        if (route.route != Routes.ChecklistMain) {
                            IconButton(onClick = { onNavigateClick(route.route) }) {
                                Icon(
                                    if (activeButton == route.route::class.qualifiedName)
                                        route.activeIcon else route.inactiveIcon,
                                    contentDescription = "Description",
                                    tint = if (activeButton == route.route::class.qualifiedName)
                                        MaterialTheme.colorScheme.primary else Color.Black
                                )
                            }
                        } else {
                            Box(
                                Modifier.size(40.dp)
                            )
                        }
                    }
                }
            },
        )
        FilledIconButton(
            modifier = Modifier
                .size(60.dp)
                .offset(y = (-28).dp)
                .shadow(2.dp, shape = CircleShape),
            onClick = { onNavigateClick(mainRoutes[2].route) },
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary
            ),
        ) {
            Icon(mainRoutes[2].activeIcon, contentDescription = "Description")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BottomBarComponentPreview() {
    BottomBarComponent()
}

@Preview(showBackground = true)
@Composable
fun BottomBarScaffoldPreview() {
    Scaffold(
        bottomBar = {
            BottomBarComponent()
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }

}