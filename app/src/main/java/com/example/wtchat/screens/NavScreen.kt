package com.example.wtchat.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wtchat.pages.HubPage
import com.example.wtchat.pages.ProfilePage
import com.example.wtchat.pages.SettingsPage
import com.example.wtchat.pages.CampaignsPage // <-- import adicionado
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOnGrey
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Notifications // <-- import adicionado
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.utils.TokenManager
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.User

@Composable
fun NavScreen(navController: NavController, authViewModel: AuthViewModel){

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

    val navItemList = listOf(
        NavItem("Home", FontAwesomeIcons.Solid.Home),
        NavItem("Campaigns", FontAwesomeIcons.Solid.Bell),
        NavItem("Profile", FontAwesomeIcons.Solid.User),
        NavItem("Settings", FontAwesomeIcons.Solid.Cog)
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
                selectedIndex = selectedIndex,
                navController,
                authViewModel
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
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val userId = tokenManager.getUser()?.id!!

    Box(modifier = modifier.fillMaxSize()) {
        when (selectedIndex) {
            0 -> HubPage(navController, authViewModel)
            1 -> CampaignsPage(navController, authViewModel)
            2 -> ProfilePage(navController, authViewModel, userId)
            3 -> SettingsPage(navController, authViewModel)
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun FloatingBottomBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(290.dp)
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
                            WTCOnGrey,
                        modifier = if(item.icon == FontAwesomeIcons.Solid.Home) Modifier.size(34.dp) else Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}