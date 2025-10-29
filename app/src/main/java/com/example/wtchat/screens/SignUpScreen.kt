package com.example.wtchat.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.Routes

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel){

    var errorMessage = remember {
        mutableStateOf("")
    }

    var crm = remember {
        mutableStateOf("")
    }

    var nome = remember {
        mutableStateOf("")
    }

    var email = remember {
        mutableStateOf("")
    }

    var senha = remember {
        mutableStateOf("")
    }

    var loading = remember {
        mutableStateOf(false)
    }

    var context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate(Routes.ConversationHubScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Loading -> loading.value = true
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // Title
            Text(
                text = "Criar Conta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Subtitle
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = crm.value,
                onValueChange = { novoValor ->
                    crm.value = novoValor
                    errorMessage.value = "" // Clear error message on input change
                },
                placeholder = {
                    Text(text = "Seu CRM")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent // Remove bottom border when unfocused
                ),
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = nome.value,
                onValueChange = { novoValor ->
                    nome.value = novoValor
                    errorMessage.value = "" // Clear error message on input change
                },
                placeholder = {
                    Text(text = "Seu nome")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent // Remove bottom border when unfocused
                ),
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = email.value,
                onValueChange = { novoValor ->
                    email.value = novoValor
                    errorMessage.value = "" // Clear error message on input change
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                placeholder = {
                    Text(text = "Seu email")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent // Remove bottom border when unfocused
                ),
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = senha.value,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { novoValor ->
                    senha.value = novoValor
                    errorMessage.value = "" // Clear error message on input change
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                placeholder = {
                    Text(text = "Sua senha")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent // Remove bottom border when unfocused
                ),
            )

            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.value.isBlank() || senha.value.isBlank() || nome.value.isBlank() || crm.value.isBlank()) {
                        errorMessage.value = "Por favor, preencha todos os campos." // Set error message
                    } else {
                        authViewModel.signup(crm.value, nome.value, email.value, senha.value)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !loading.value
            ) {
                Text(text = "Criar conta", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Registration Button
            TextButton(
                onClick = {
                    navController.navigate(Routes.LoginScreen)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading.value
            ) {
                Text(text = "Já tem uma conta? Faça login", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}