package com.example.wtchat.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.wtchat.pages.HubPage
import com.example.wtchat.pages.ProfilePage
import com.example.wtchat.pages.SettingsPage

@Composable
fun ConversationHubScreen(modifier: Modifier = Modifier, navController: NavController) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Settings", Icons.Default.Settings),
        NavItem("Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) {
        ContentScreen(modifier = modifier.padding(it), selectedIndex)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int) {
    when(selectedIndex){
        0-> HubPage()
        1-> ProfilePage()
        2-> SettingsPage()
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)
