package com.example.grocerychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.grocerychecklist.ui.component.BottomBarComponent
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.checklist.checklistDestination
import com.example.grocerychecklist.ui.screen.dashboard.dashboardDestination
import com.example.grocerychecklist.ui.screen.history.historyDestination
import com.example.grocerychecklist.ui.screen.item.itemDestination
import com.example.grocerychecklist.ui.screen.settings.settingsDestination
import com.example.grocerychecklist.ui.theme.GroceryChecklistTheme

class MainActivity : ComponentActivity() {


    private lateinit var navigator: Navigator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Used to clear DB data
//        val dbRepo = GroceryChecklistApp.appModule.databaseRepository
//        lifecycleScope.launch (Dispatchers.IO){
//            dbRepo.clearRoomDB()
//        }

        setContent {
            GroceryChecklistTheme {
                val navController = rememberNavController()

                DisposableEffect(key1 = navController) {
                    navigator = GroceryChecklistApp.appModule.navigator
                    navigator.setController(navController)
                    onDispose {
                        navigator.clear()
                    }
                }

                val bottomNavRoutes = listOf(
                    Routes.DashboardMain::class.qualifiedName,
                    Routes.ChecklistMain::class.qualifiedName,
                    Routes.ItemMain::class.qualifiedName,
                    Routes.HistoryMain::class.qualifiedName,
                    Routes.SettingsMain::class.qualifiedName
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route


                Scaffold (
                    bottomBar = {
                        if(currentRoute in bottomNavRoutes) {
                            BottomBarComponent(
                                activeButton = currentRoute,
                                onNavigateClick = { route -> navController.navigate(route) }
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets(0.dp)
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.DashboardMain,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        dashboardDestination()
                        checklistDestination()
                        itemDestination()
                        historyDestination(navController)
                        settingsDestination()
                    }
                }

            }
        }
    }
}

