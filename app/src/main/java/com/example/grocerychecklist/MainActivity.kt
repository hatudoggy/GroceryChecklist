package com.example.grocerychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.grocerychecklist.ui.component.BottomBarComponent
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.ui.screen.checklist.checklistDestination
import com.example.grocerychecklist.ui.screen.dashboard.dashboardDestination
import com.example.grocerychecklist.ui.screen.history.historyDestination
import com.example.grocerychecklist.ui.screen.item.itemDestination
import com.example.grocerychecklist.ui.screen.settings.settingsDestination
import com.example.grocerychecklist.ui.theme.GroceryChecklistTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

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
                Scaffold (
                    bottomBar = {
                        BottomBarComponent(
                            onNavigateClick = { route -> navController.navigate(route)}
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.DashboardMain,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        dashboardDestination(navController)
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

