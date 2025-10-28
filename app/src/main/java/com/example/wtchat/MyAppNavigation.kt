package com.example.wtchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtchat.screens.ConversationHubScreen
import com.example.wtchat.screens.LoginScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.LoginScreen
    ) {
        composable(Routes.LoginScreen) {
            LoginScreen(navController)
        }

        composable(Routes.ConversationHubScreen) {
            ConversationHubScreen(
                modifier = modifier,
                navController = navController
            )
        }
    }
}