package com.example.wtchat.screens

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
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
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.User
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait

@Composable
fun UsersScreen(navController: NavController, authViewModel: AuthViewModel){

    val authState = authViewModel.authState.observeAsState()

    var context = LocalContext.current
    val tokenManager = TokenManager(context)

    val chatsService = RetrofitInstance.getInstance().chatsService
    val usersService = RetrofitInstance.getInstance().usersService

    val isLoading = remember { mutableStateOf(true) }

    var users = remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    var selectedRole = remember {
        mutableStateOf("")
    }

    var expandedRole = remember {
        mutableStateOf(false)
    }

    var selectedSegment = remember {
        mutableStateOf("")  // Valor padrão
    }

    var expandedSegment = remember {
        mutableStateOf(false)
    }

    var knownChats = remember {
        mutableStateOf<List<ChatModel>>(emptyList())
    }

    var pesquisa = remember {
        mutableStateOf("")
    }

    fun askToCreateChat(user: UserModel){
        var hasChat = false
        var hasChatId = ""
        knownChats.value.forEach { chat ->
            if(chat.privateChatMembers.contains(user.id) && chat.privateChatMembers.contains(tokenManager.getUser()?.id!!)){
                hasChat = true
                hasChatId = chat.id
            }
        }
        if(hasChat){
            navController.navigate(Routes.ConversationScreen + "/${hasChatId}/${user.name}")
        } else {
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
    }

    LaunchedEffect(authState.value, pesquisa.value, selectedRole.value, selectedSegment.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }

            is AuthState.Authenticated -> {
                isLoading.value = true
                val currentUser = tokenManager.getUser()
                val allChats = chatsService.getChats()
                val filteredChats = allChats.filter { chat ->
                    if(chat.privateChatMembers.isNotEmpty()) {
                        chat.privateChatMembers.contains(currentUser?.id)
                    } else {
                        false
                    }
                }

                knownChats.value = filteredChats
                println("just so you know: "+knownChats.value)

                val allUsers = usersService.getAllUsers()

                println("Selected role: ${selectedRole.value} | Selected segment: ${selectedSegment.value} | Pesquisa: ${pesquisa.value}")

                users.value = allUsers.filter { user ->

                    println("Checking user: ${user.name} | Roles: ${user.roles} | Segment: ${user.segment}")

                    user.id != currentUser?.id && user.roles.toString().contains(selectedRole.value)
                            && user.segment.contains(selectedSegment.value)
                            && (user.name.contains(pesquisa.value, ignoreCase = true) || user.username.contains(pesquisa.value, ignoreCase = true))

                }

                isLoading.value = false

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
                    Text(text = "Pesquisar nome ou código CRM")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            expandedRole.value = !expandedRole.value
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WTCGrey),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Função",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = when(selectedRole.value){
                                    "ADMIN" -> "Operador"
                                    "USER" -> "Usuário"
                                    else -> "Todos"
                                },
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedRole.value,
                        onDismissRequest = { expandedRole.value = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                selectedRole.value = ""
                                expandedRole.value = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Usuário") },
                            onClick = {
                                selectedRole.value = "USER"
                                expandedRole.value = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Operador") },
                            onClick = {
                                selectedRole.value = "ADMIN"
                                expandedRole.value = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.size(15.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            expandedSegment.value = !expandedSegment.value
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WTCGrey),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Segmento",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = when (selectedSegment.value) {
                                    "RETAIL" -> "Varejo"
                                    "EDUCATION" -> "Educação"
                                    "FINANCE" -> "Financeiro"
                                    "TECHNOLOGY" -> "Tecnologia"
                                    "HEALTHCARE" -> "Saúde"
                                    else -> "Todos"
                                },
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedSegment.value,
                        onDismissRequest = { expandedSegment.value = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        listOf(
                            "",
                            "RETAIL",
                            "HEALTHCARE",
                            "EDUCATION",
                            "FINANCE",
                            "TECHNOLOGY"
                        ).forEach { seg ->
                            DropdownMenuItem(
                                text = { Text(when(seg) {
                                    "RETAIL" -> "Varejo"
                                    "EDUCATION" -> "Educação"
                                    "FINANCE" -> "Financeiro"
                                    "TECHNOLOGY" -> "Tecnologia"
                                    "HEALTHCARE" -> "Saúde"
                                    else -> "Todos"
                                })},
                                onClick = {
                                    selectedSegment.value = seg
                                    expandedSegment.value = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            if(!isLoading.value && users.value.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    contentPadding = PaddingValues(top = 30.dp,bottom = 125.dp)
                ) {
                    items(users.value) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    askToCreateChat(item)
                                }),
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Box(
                                modifier = Modifier
                                    .background(WTCBlue, RoundedCornerShape(200.dp))
                                    .size(65.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.User,
                                    contentDescription = "Chat Icon",
                                    modifier = Modifier.size(28.dp),
                                    tint = WTCBackground
                                )
                            }

                            Spacer(modifier = Modifier.size(20.dp))

                            Column {
                                Text(
                                    style = MaterialTheme.typography.titleMedium,
                                    text = item.name
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                if (!item.roles.contains("ROLE_ADMIN")) {
                                    Text(
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        text = when (item.segment.replace("SEGMENT_", "")) {
                                            "RETAIL" -> "Varejo"
                                            "EDUCATION" -> "Educação"
                                            "FINANCE" -> "Financeiro"
                                            "TECHNOLOGY" -> "Tecnologia"
                                            "HEALTHCARE" -> "Saúde"
                                            else -> "Comum"
                                        }
                                    )
                                } else {
                                    Text(
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        text = "Operador(a)"
                                    )
                                }
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    text = "Cód. CRM: " + item.username,
                                    color = Color.Gray
                                )

                            }
                        }
                    }
                }
            } else if(isLoading.value) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = WTCBlue)
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        text = "Carregando..."
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Usuário não encontrado.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}