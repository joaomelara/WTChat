package com.example.wtchat.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.Routes
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.ChatModel
import com.example.wtchat.models.UserModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.utils.TokenManager

@Composable
fun HubPage(navController: NavController ,authViewModel: AuthViewModel){

    var conversas = remember {
        mutableStateOf<List<ChatModel>>(emptyList())
    }

    val chatsService = RetrofitInstance.getInstance().chatsService
    val usersService = RetrofitInstance.getInstance().usersService

    var context = LocalContext.current
    val tokenManager = TokenManager(context)

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                try {
                    val currentUser = tokenManager.getUser()
                    val chats = chatsService.getChats()

                    val filteredChats = chats.filter { chat ->
                        // If it's a private chat (privateChatMembers is not empty)
                        if(chat.privateChatMembers.isNotEmpty()) {
                            // Only show if current user is a participant
                            chat.privateChatMembers.contains(currentUser?.id)
                        } else {
                            // It's a group chat (privateChatMembers is empty)
                            // Show if user is admin OR user's segment matches chat's segment
                            currentUser?.roles?.contains("ROLE_ADMIN") == true ||
                                    currentUser?.segment == chat.segment
                        }
                    }

                    val updatedChats = filteredChats.map { chat ->
                        if(chat.privateChatMembers.contains(currentUser?.id)){
                            val otherUserId = chat.privateChatMembers.first { it != currentUser?.id }
                            val tempUserModel: UserModel = usersService.getUserById(otherUserId)
                            chat.copy(name = tempUserModel.name)
                        } else {
                            chat
                        }
                    }
                    conversas.value = updatedChats
                } catch (e: Exception) {
                    e.printStackTrace()
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
                text = "Conversas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(45.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WTCBlue, RoundedCornerShape(200.dp))
                    .clickable(onClick = {
                        navController.navigate(Routes.UsersScreen)
                    })
                    .padding( 20.dp, 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

                ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    color = WTCGrey,
                    text = "Iniciar nova conversa"
                )


                Box(
                    modifier = Modifier.background(WTCBlue, RoundedCornerShape(200.dp))
                        .size(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(40.dp),
                        tint = WTCBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            LazyColumn {
                items(conversas.value) { item ->
                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = {
                                navController.navigate(Routes.ConversationScreen+"/"+item.id+"/"+item.name)
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
                }
            }

        }
    }

}

