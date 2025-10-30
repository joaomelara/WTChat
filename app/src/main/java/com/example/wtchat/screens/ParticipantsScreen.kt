package com.example.wtchat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.models.MessageModel
import com.example.wtchat.models.UserModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ParticipantsScreen(navController: NavController ,authViewModel: AuthViewModel, chatId: String){
    var context = LocalContext.current

    val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    val authState = authViewModel.authState.observeAsState()

    var users = remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                Firebase.firestore.collection("chats").document(chatId).get().addOnCompleteListener {
                        if(it.isSuccessful) {
                            val userIds = it.result.get("participantes") as List<String>
                            Firebase.firestore.collection("users").whereIn("uid", userIds)
                                .get().addOnCompleteListener { it2 ->
                                    if(it2.isSuccessful) {
                                        val results = it2.result.documents.mapNotNull { doc ->
                                            doc.toObject(UserModel::class.java)
                                        }
                                        users.value = results
                                    }
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

            // Title
            Text(
                text = "Participantes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            LazyColumn {
                items(users.value) { item ->
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = {
                                navController.navigate(Routes.ProfilePage+"/"+item.uid+"/"+item.nome)
                            }),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Box(
                            modifier = Modifier.background(WTCBlue, RoundedCornerShape(200.dp))
                                .size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "User Icon", modifier =  Modifier.size(30.dp), tint = WTCBackground)
                        }

                        Spacer(modifier = Modifier.size(20.dp))

                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = item.nome
                        )

                    }
                }
            }

        }
    }

}