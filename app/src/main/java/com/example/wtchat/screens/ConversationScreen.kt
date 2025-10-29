package com.example.wtchat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.models.ChatModel
import com.example.wtchat.models.MessageModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ConversationScreen(navController: NavController ,authViewModel: AuthViewModel, chatId: String){

    var context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    var mensagens = remember {
        mutableStateOf<List<MessageModel>>(emptyList())
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                Firebase.firestore.collection("chats").document(chatId)
                    .collection("messages").orderBy("data").addSnapshotListener { snapshot, e ->
                        if(e == null && snapshot != null) {
                            val results = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(MessageModel::class.java)
                            }
                            mensagens.value = results
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

            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(mensagens.value) {item ->
                    Spacer(modifier = Modifier.size(30.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if(item.autor == FirebaseAuth.getInstance().currentUser?.uid) {
                            Alignment.End
                        } else {
                            Alignment.Start
                        }
                    ) {
                        Box(
                            modifier = Modifier.background(WTCGrey, RoundedCornerShape(50.dp))
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                text = item.texto,
                            )
                        }
                    }
                }
            }
        }
    }
}