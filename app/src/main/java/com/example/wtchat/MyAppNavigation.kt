package com.example.wtchat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtchat.screens.ConversationHubScreen
import com.example.wtchat.screens.LoginScreen

@Composable
fun MyAppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LoginScreen, builder = {
        composable(Routes.LoginScreen) {
            LoginScreen(navController)
        }

        //some data passing tests
        composable(Routes.ConversationHubScreen+"/{name}"){
            val name = it.arguments?.getString("name")
            ConversationHubScreen(name?:"No Name")
        }
    })
}