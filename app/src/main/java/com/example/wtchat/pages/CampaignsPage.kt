package com.example.wtchat.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wtchat.Routes
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.CampaignModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.utils.TokenManager
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun CampaignsPage(navController: NavController, authViewModel: AuthViewModel) {

    var campaigns = remember {
        mutableStateOf<List<CampaignModel>>(emptyList())
    }

    var isLoading = remember {
        mutableStateOf(false)
    }

    var selectedCampaign = remember {
        mutableStateOf<CampaignModel?>(null)
    }

    val campaignService = RetrofitInstance.getInstance().campaignService
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val coroutineScope = rememberCoroutineScope()

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                isLoading.value = true
                try {
                    campaigns.value = campaignService.getCampaigns()
                } catch (e: Exception) {
                    println("Erro ao carregar campanhas: ${e.message}")
                } finally {
                    isLoading.value = false
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
                text = "Campanhas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading.value) {
                CircularProgressIndicator(color = WTCBlue)
            } else if (campaigns.value.isEmpty()) {
                Text(
                    text = "Nenhuma campanha disponível",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WTCGrey
                )
            } else {
                LazyColumn {
                    items(campaigns.value) { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            onDeleteClick = {
                                coroutineScope.launch {
                                    try {
                                        campaignService.deleteCampaign(campaign.id)
                                        campaigns.value = campaignService.getCampaigns()
                                    } catch (e: Exception) {
                                        println("Erro ao deletar campanha: ${e.message}")
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: CampaignModel,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = WTCGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = campaign.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WTCBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = campaign.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = campaign.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Data: ${campaign.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = WTCOrange
                        )

                        Text(
                            text = "Por: ${campaign.createdBy}",
                            style = MaterialTheme.typography.labelSmall,
                            color = WTCOrange
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Deletar campanha",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

