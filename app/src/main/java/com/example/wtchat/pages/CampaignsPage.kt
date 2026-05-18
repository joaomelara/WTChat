package com.example.wtchat.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.wtchat.viewmodels.AuthState
import com.example.wtchat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun CampaignsPage(navController: NavController, authViewModel: AuthViewModel) {

    val campaigns = remember { mutableStateOf<List<CampaignModel>>(emptyList()) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val campaignService = RetrofitInstance.getInstance().campaignService
    val coroutineScope = rememberCoroutineScope()
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Routes.LoginScreen) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
            is AuthState.Authenticated -> {
                isLoading.value = true
                errorMessage.value = null
                try {
                    campaigns.value = campaignService.getCampaigns()
                } catch (e: Exception) {
                    errorMessage.value = "Erro ao carregar campanhas"
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Campanhas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WTCBlue
            )

            Spacer(modifier = Modifier.height(30.dp))

            when {
                isLoading.value -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WTCBlue)
                    }
                }

                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage.value!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isLoading.value = true
                                        errorMessage.value = null
                                        try {
                                            campaigns.value = campaignService.getCampaigns()
                                        } catch (e: Exception) {
                                            errorMessage.value = "Erro ao carregar campanhas"
                                        } finally {
                                            isLoading.value = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WTCBlue)
                            ) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }

                campaigns.value.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma campanha disponível",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WTCGrey
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(
                            items = campaigns.value,
                            key = { it.id }
                        ) { campaign ->
                            CampaignCard(
                                campaign = campaign,
                                onEditClick = {
                                    navController.navigate(Routes.EditCampaignScreen + "/${campaign.id}")
                                },
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: CampaignModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                // Conteúdo principal
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = campaign.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WTCBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = campaign.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = campaign.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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
                            text = "Por: ${campaign.createdBy ?: "N/A"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = WTCOrange
                        )
                    }

                    if (campaign.segments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Segmentos: ${campaign.segments.joinToString(", ") { it.removePrefix("SEGMENT_") }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = WTCBlue
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar campanha",
                            tint = WTCBlue,
                            modifier = Modifier.size(24.dp)
                        )
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
}