package com.example.wtchat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.models.ChatModel
import com.example.wtchat.models.MessageModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Composable
fun ConversationScreen(navController: NavController ,authViewModel: AuthViewModel, chatId: String, chatNome: String, userNome: String){

    var context = LocalContext.current

    val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    val authState = authViewModel.authState.observeAsState()

    var listState = rememberLazyListState()

    var novaMensagem = remember {
        mutableStateOf("")
    }

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

    LaunchedEffect(mensagens.value) {
        if(mensagens.value.isNotEmpty()){
            listState.animateScrollToItem(mensagens.value.lastIndex)
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (messages, chatBox, headerBox) = createRefs()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WTCGrey)
                .height(60.dp)
                .padding(horizontal = 20.dp)
                .constrainAs(headerBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(messages.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = chatNome
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .constrainAs(messages) {
                    top.linkTo(headerBox.bottom)
                    bottom.linkTo(chatBox.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                },
            state = listState
        ) {
            items(mensagens.value) { item ->
                ChatItem(item)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .constrainAs(chatBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                shape = RoundedCornerShape(20.dp),
                value = novaMensagem.value,
                onValueChange = { novoValor ->
                    novaMensagem.value = novoValor
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                placeholder = {
                    Text(text = "Escreva uma mensagem")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                ),
                )
            IconButton(
                modifier = Modifier.background(WTCBlue, RoundedCornerShape(100.dp)),
                onClick = {
                    val messageLocalDateTime = LocalDateTime.now()
                    val messageDate = Date.from(messageLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())

                    val message = MessageModel(userId, userNome, novaMensagem.value, messageDate)

                    Firebase.firestore.collection("chats").document(chatId).collection("messages").add(message)
                    novaMensagem.value = ""
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send Icon", tint = WTCBackground
                )
            }
        }
    }
}

@Composable
fun ChatItem(item: MessageModel) {

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    Spacer(modifier = Modifier.size(10.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (item.autor == userId) Alignment.End else Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(start = if (item.autor == userId) 0.dp else 5.dp,
                end = if (item.autor == userId) 5.dp else 0.dp),
            style = MaterialTheme.typography.bodySmall,
            text = if (item.autor == userId) "Você" else item.nome
        )
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (item.autor == userId) 48f else 0f,
                        bottomEnd = if (item.autor == userId) 0f else 48f
                    )
                )
                .background(WTCGrey)
                .padding(16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = item.texto
            )
        }
    }
    Spacer(modifier = Modifier.size(10.dp))
}