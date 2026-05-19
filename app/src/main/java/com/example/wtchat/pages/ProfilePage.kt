package com.example.wtchat.pages

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.AnnotationModel
import com.example.wtchat.models.UserModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.utils.TokenManager
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.User

@Composable
fun ProfilePage(navController: NavController ,authViewModel: AuthViewModel, userId: String){

    var annotations = remember {
        mutableStateOf("")
    }

    var context = LocalContext.current
    val tokenManager = TokenManager(context)
    val localUser = tokenManager.getUser()
    val authState = authViewModel.authState.observeAsState()
    val usersService = RetrofitInstance.getInstance().usersService

    val safeUser = remember {
        mutableStateOf(UserModel())
    }

    val userIdStored = tokenManager.getUser()?.id ?: "Error"

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                if (userId != userIdStored) {
                    try {
                        annotations.value = tokenManager.getAnnotation(userId)?.annotationText ?: ""
                        safeUser.value = usersService.getUserById(userId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    safeUser.value = localUser!!
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .background(WTCBlue, RoundedCornerShape(200.dp))
                    .size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = FontAwesomeIcons.Solid.User, contentDescription = "Profile Icon", modifier =  Modifier.size(42.dp), tint = WTCBackground)
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = safeUser.value.name
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .background(WTCOrange, shape = RoundedCornerShape(50.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = safeUser.value.email
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "Informações"
            )
            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WTCGrey, RoundedCornerShape(20.dp))
                    .padding(20.dp),
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "CRM:"
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = safeUser.value.username
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if(!safeUser.value.roles.contains("ROLE_ADMIN")) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Segmento:"
                    )
                        val segment = safeUser.value.segment
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = when (segment.replace("SEGMENT_", "")) {
                                "RETAIL" -> "Varejo"
                                "EDUCATION" -> "Educação"
                                "FINANCE" -> "Financeiro"
                                "TECHNOLOGY" -> "Tecnologia"
                                "HEALTHCARE" -> "Saúde"
                                else -> ""
                            }
                        )
                    } else {
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Cargo:"
                        )
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Operador"
                        )
                    }
                }
            }
            if(userId != userIdStored){
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = if (annotations.value.isNotEmpty()) "Anotações" else ""
                    )
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    value = annotations.value,
                    onValueChange = { novoValor ->
                        annotations.value = novoValor
                        tokenManager.saveAnnotation(AnnotationModel(userId, annotations.value))
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
