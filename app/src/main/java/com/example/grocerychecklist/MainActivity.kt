package com.example.grocerychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.ui.component.BottomBarComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.checklist.checklistDestination
import com.example.grocerychecklist.ui.screen.dashboard.dashboardDestination
import com.example.grocerychecklist.ui.screen.history.historyDestination
import com.example.grocerychecklist.ui.screen.item.itemDestination
import com.example.grocerychecklist.ui.screen.settings.settingsDestination
import com.example.grocerychecklist.ui.theme.GroceryChecklistTheme

class MainActivity : ComponentActivity() {
//    val db = Room.databaseBuilder(
//        applicationContext,
//        AppDatabase::class.java, "GroceryAppDB"
//    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryChecklistTheme {
                val navController = rememberNavController()
                Scaffold (
                    bottomBar = {
                        BottomBarComponent(
                            onNavigateClick = { route -> navController.navigate(route)}
                        )
                    },
                    contentWindowInsets = WindowInsets(0.dp),
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.DashboardMain,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        dashboardDestination()
                        checklistDestination()
                        itemDestination()
                        historyDestination()
                        settingsDestination()
                    }
                }

            }
        }
    }
}

