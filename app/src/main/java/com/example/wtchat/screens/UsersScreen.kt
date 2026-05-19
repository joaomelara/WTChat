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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.ChatModel
import com.example.wtchat.models.UserModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.utils.TokenManager
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait

@Composable
fun UsersScreen(navController: NavController, authViewModel: AuthViewModel){

    val authState = authViewModel.authState.observeAsState()

    var context = LocalContext.current
    val tokenManager = TokenManager(context)

    val chatsService = RetrofitInstance.getInstance().chatsService
    val usersService = RetrofitInstance.getInstance().usersService

    var users = remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    var knownChats = remember {
        mutableStateOf<List<ChatModel>>(emptyList())
    }

    var pesquisa = remember {
        mutableStateOf("")
    }

    fun askToCreateChat(user: UserModel){
        var response = ChatModel()
        val chatModel = ChatModel(
            name = "conversa",
            segment = user.segment,
            privateChatMembers = listOf(user.id, tokenManager.getUser()?.id!!)
        )
        runBlocking {
            try {
                response = chatsService.createChat(chatModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if(response.id.isNotEmpty()) {
            navController.navigate(Routes.ConversationScreen + "/${response.id}/${user.name}")
        } else {
            println("Erro ao criar conversa")
        }
    }

    LaunchedEffect(authState.value, pesquisa.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }

            is AuthState.Authenticated -> {
                val currentUser = tokenManager.getUser()
                val allUsers = usersService.getAllUsers()
                users.value = allUsers.filter { user ->
                    user.id != currentUser?.id && user.name.contains(pesquisa.value, ignoreCase = true)
                            || user.segment.contains(pesquisa.value, ignoreCase = true)
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
                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Iniciar nova conversa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(45.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = pesquisa.value,
                onValueChange = { novoValor ->
                    pesquisa.value = novoValor
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                placeholder = {
                    Text(text = "Pesquisar por nome ou segmento")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            LazyColumn {
                items(users.value) { item ->
                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = {
                                askToCreateChat(item)
                            }),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Box(
                            modifier = Modifier.background(WTCBlue, RoundedCornerShape(200.dp))
                                .size(65.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Rounded.Person, contentDescription = "Chat Icon", modifier =  Modifier.size(40.dp), tint = WTCBackground)
                        }

                        Spacer(modifier = Modifier.size(20.dp))

                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = item.name
                        )

                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }

        }
    }
}