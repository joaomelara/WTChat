package com.example.wtchat.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.models.UserModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ProfilePage(navController: NavController ,authViewModel: AuthViewModel, origin: String, userId: String, userName: String = ""){

    var anotations = remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()

    val safeName = remember {
        mutableStateOf("")
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                if (userName.isEmpty() && userId != "Error") {
                    Firebase.firestore.collection("users").document(userId).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                safeName.value = it.result.get("nome").toString()
                            }
                        }
                }
            }
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WTCBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .background(WTCBlue, RoundedCornerShape(200.dp))
                    .size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Chat Icon", modifier =  Modifier.size(60.dp), tint = WTCBackground)
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = userName.ifEmpty { safeName.value }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .background(WTCOrange, shape = RoundedCornerShape(50.dp))
            )
            Spacer(modifier = Modifier.height(40.dp))
            if(userId != FirebaseAuth.getInstance().currentUser?.uid!!){

                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = if (anotations.value.isNotEmpty()) "Anotações" else ""
                    )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    value = anotations.value,
                    onValueChange = { novoValor ->
                        anotations.value = novoValor
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    placeholder = {
                        Text(text = "Anotações")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                        unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                        unfocusedContainerColor = WTCGrey,
                        focusedContainerColor = WTCGrey
                    ),
                    maxLines = 15
                    )
            }
        }
    }
}
