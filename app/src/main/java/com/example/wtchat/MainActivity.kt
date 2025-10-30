package com.example.wtchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.viewmodels.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel : AuthViewModel by viewModels()
        val chatViewModel: ChatViewModel by viewModels()
        setContent {
            MyAppNavigation(authViewModel)
        }
    }
}
