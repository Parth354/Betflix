package com.example.betflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("details/{itemId}") { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                    DetailsScreen(itemId, navController)
                }
            }
        }
    }
}