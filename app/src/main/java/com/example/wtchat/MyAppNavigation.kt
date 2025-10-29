package com.example.wtchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtchat.screens.ConversationHubScreen
import com.example.wtchat.screens.LoadingScreen
import com.example.wtchat.screens.LoginScreen
import com.example.wtchat.screens.SignUpScreen
import com.example.wtchat.viewmodels.AuthViewModel

@Composable
fun MyAppNavigation(authViewModel: AuthViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LoadingScreen, builder = {
        composable(Routes.LoadingScreen) {
            LoadingScreen(navController, authViewModel)
        }

        composable(Routes.LoginScreen) {
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.SignUpScreen) {
            SignUpScreen(navController, authViewModel)
        }

        //some data passing tests
        composable(Routes.ConversationHubScreen){
            ConversationHubScreen(navController, authViewModel)
        }
    }
}