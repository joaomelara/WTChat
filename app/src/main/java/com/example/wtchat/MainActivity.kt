package com.example.wtchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Retrofit with context for token interceptor
        RetrofitInstance.initialize(this)
        
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            MyAppNavigation(authViewModel)
        }
    }
}
