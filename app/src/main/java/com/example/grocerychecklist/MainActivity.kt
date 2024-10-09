package com.example.grocerychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.ui.screen.Dashboard
import com.example.grocerychecklist.ui.screen.dashboardDestination
import com.example.grocerychecklist.ui.theme.GroceryChecklistTheme

class MainActivity : ComponentActivity() {
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "GroceryAppDB"
    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryChecklistTheme {
                val navController = rememberNavController()
                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Dashboard,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        dashboardDestination()
                    }
                }

            }
        }
    }
}

