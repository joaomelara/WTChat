package com.example.wtchat.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.R
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.Routes
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.ui.theme.WTCRed

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

    var selectedSegment = remember {
        mutableStateOf("RETAIL")  // Valor padrão
    }

    var expandedSegment = remember {
        mutableStateOf(false)
    }

    var selectedRole = remember {
        mutableStateOf("ROLE_USER")  // Valor padrão
    }

    var expandedRole = remember {
        mutableStateOf(false)
    }

    var loading = remember {
        mutableStateOf(false)
    }

    var context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> {
                navController.navigate(Routes.ConversationHubScreen) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
                loading.value = false
            }
            is AuthState.Loading -> loading.value = true
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
                ).show()
                loading.value = false
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
                .padding(20.dp, 0.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(30.dp))
            /* Image(
                painter = painterResource(id = R.drawable.wtchatlogo), // Replace with your image name
                contentDescription = null, // Provide a description for accessibility
                modifier = Modifier.height(50.dp)
                    .width(150.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))*/

            Text(
                text = "Criar conta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .background(WTCOrange, shape = RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(30.dp))

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
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = nome.value,
                onValueChange = { novoValor ->
                    if (novoValor.length <= 25) {
                        nome.value = novoValor
                        errorMessage.value = ""
                    }
                },
                placeholder = {
                    Text(text = "Seu nome")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Remove bottom border when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                    unfocusedContainerColor = WTCGrey,
                    focusedContainerColor = WTCGrey
                ),
            )

             Spacer(modifier = Modifier.height(20.dp))

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
                     unfocusedIndicatorColor = Color.Transparent, // Remove bottom border when unfocused
                     unfocusedContainerColor = WTCGrey,
                     focusedContainerColor = WTCGrey
                 ),
             )

               Spacer(modifier = Modifier.height(20.dp))

              Box(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = {
                          expandedRole.value = !expandedRole.value
                      },
                      modifier = Modifier.fillMaxWidth(),
                      colors = ButtonDefaults.buttonColors(containerColor = WTCGrey),
                      shape = RoundedCornerShape(20.dp)
                  ) {
                      Text(
                          text = "Função: ${ selectedRole.value.replace("ROLE_", "")
                              .replace("ADMIN", "Operador").replace("USER", "Usuário") }",
                          color = Color.Black,
                          modifier = Modifier.weight(1f)
                      )
                  }

                 DropdownMenu(
                     expanded = expandedRole.value,
                     onDismissRequest = { expandedRole.value = false },
                     modifier = Modifier.fillMaxWidth(0.9f)
                 ) {
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

            if(selectedRole.value != "ADMIN"){
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            expandedSegment.value = !expandedSegment.value
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WTCGrey),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Segmento: ${ selectedSegment.value }",
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedSegment.value,
                        onDismissRequest = { expandedSegment.value = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        listOf("RETAIL", "HEALTHCARE", "EDUCATION", "FINANCE", "TECHNOLOGY").forEach { seg ->
                            DropdownMenuItem(
                                text = { Text(seg) },
                                onClick = {
                                    selectedSegment.value = seg
                                    expandedSegment.value = false
                                }
                            )
                        }
                    }
                }
            }

            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = WTCRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

              Button(
                  onClick = {
                      if (email.value.isBlank() || senha.value.isBlank() || nome.value.isBlank() || crm.value.isBlank()) {
                          errorMessage.value = "Por favor, preencha todos os campos." // Set error message
                      } else {
                          authViewModel.signup(crm.value, nome.value, email.value, senha.value, selectedSegment.value, setOf(selectedRole.value))
                      }
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = WTCBlue),
                  enabled = !loading.value
              ) {
                  Text(text = "Criar conta", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
              }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = {
                    navController.navigate(Routes.LoginScreen)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading.value
            ) {
                Text(text = "Já tem uma conta? Faça login", color = WTCOrange)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}