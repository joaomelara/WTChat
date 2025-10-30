package com.example.wtchat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.wtchat.data.repository.LastAcessRepository
import com.example.wtchat.screens.ConversationHubScreen
import com.example.wtchat.screens.LoadingScreen
import com.example.wtchat.screens.LoginScreen
import com.example.wtchat.screens.SignUpScreen
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.viewmodels.ChatViewModel
import com.example.wtchat.data.database.LocalDatabase

@Composable
fun MyAppNavigation(authViewModel: AuthViewModel){
    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val db = remember {
        Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "local_db"
        ).build()
    }

    // ✅ Cria o repositório e o ChatViewModel
    val lastAccessRepository = remember { LastAcessRepository(db) }
    val chatViewModel = remember { ChatViewModel(lastAccessRepository) }

    NavHost(navController = navController, startDestination = Routes.LoadingScreen) {
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
            ConversationHubScreen(navController, authViewModel, chatViewModel)
        }
    }
}