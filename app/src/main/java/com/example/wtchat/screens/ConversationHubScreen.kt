package com.example.wtchat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wtchat.pages.HubPage
import com.example.wtchat.pages.ProfilePage
import com.example.wtchat.pages.SettingsPage
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCNavIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.example.wtchat.Routes

@Composable
fun ConversationHubScreen(navController: NavController ,authViewModel: AuthViewModel){

    var context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen){
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            else -> Unit
        }
    }
    
    
     val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Profile", Icons.Default.Person),
        NavItem("Settings", Icons.Default.Settings)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = WTCBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContentScreen(
                modifier = Modifier.fillMaxSize(),
                selectedIndex = selectedIndex
            )

            FloatingBottomBar(
                items = navItemList,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun FloatingBottomBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(220.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(WTCGrey)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                IconButton(onClick = { onItemSelected(index) }) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (index == selectedIndex)
                            WTCBlue
                        else
                            WTCNavIcons,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
    

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Conversation Screen and nameTest is gaming")
        Button(
            onClick = {
                authViewModel.signout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = authState != AuthState.Loading
        ) {
            Text(text = "Sair", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
        }
    }
}


@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedIndex) {
            0 -> HubPage()
            1 -> ProfilePage()
            2 -> SettingsPage()
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)
