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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOnGreyLight
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsPage(navController: NavController ,authViewModel: AuthViewModel){

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> {
                navController.navigate(Routes.LoginScreen) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
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
                text = "Configurações",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(30.dp))
            Column(
                modifier = Modifier.
                background(WTCGrey, RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Conta"
                    )

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Right Arrow",
                        tint = WTCOnGreyLight
                    )

                }
                Spacer(modifier = Modifier.size(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(WTCOnGreyLight, shape = RoundedCornerShape(50.dp))
                )
                Spacer(modifier = Modifier.size(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Notificações"
                    )

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Right Arrow",
                        tint = WTCOnGreyLight
                    )

                }
                Spacer(modifier = Modifier.size(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(WTCOnGreyLight, shape = RoundedCornerShape(50.dp))
                )
                Spacer(modifier = Modifier.size(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Privacidade"
                    )

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Right Arrow",
                        tint = WTCOnGreyLight
                    )

                }
                Spacer(modifier = Modifier.size(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(WTCOnGreyLight, shape = RoundedCornerShape(50.dp))
                )
                Spacer(modifier = Modifier.size(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Ajuda"
                    )

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Right Arrow",
                        tint = WTCOnGreyLight
                    )

                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WTCOrange),
                onClick = {
                    authViewModel.signout()
                }
            ) {
                Text(text = "Sair", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
