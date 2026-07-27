package com.shuleka.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{postId}") {
        fun createRoute(postId: String) = "detail/$postId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.Detail.createRoute(postId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            DetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
