package com.example.wtchat.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wtchat.Routes

@Composable
fun LoginScreen(navController: NavController){
    Column (
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "LoginScreen")
        Button(onClick = {
            navController.navigate(Routes.ConversationHubScreen)
        }) {
            Text(text = "GOTO Conversations")

        }
    }
}
