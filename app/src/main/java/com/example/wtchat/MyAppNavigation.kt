package com.example.wtchat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtchat.screens.ConversationHubScreen
import com.example.wtchat.screens.ConversationScreen
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

        composable(Routes.ConversationScreen + "/{chatId}/{chatNome}/{userNome}") {
            val chatId = it.arguments?.getString("chatId") ?: "Error"
            val chatNome = it.arguments?.getString("chatNome") ?: "Unnamed Chat"
            val userNome = it.arguments?.getString("userNome") ?: "Anônimo"
            ConversationScreen(navController, authViewModel, chatId, chatNome, userNome)
        }

    })
}