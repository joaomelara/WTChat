package com.example.wtchat.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel

@Composable
fun LoadingScreen(navController: NavController, authViewModel: AuthViewModel){
    var context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate(Routes.ConversationHubScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            else -> Unit
        }
    }
}