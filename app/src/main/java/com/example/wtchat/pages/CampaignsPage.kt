package com.example.wtchat.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Comment
import compose.icons.fontawesomeicons.solid.Feather
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Trash
import kotlinx.coroutines.launch

@Composable
fun CampaignsPage(navController: NavController, authViewModel: AuthViewModel) {

    val campaigns = remember { mutableStateOf<List<CampaignModel>>(emptyList()) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isAdmin = remember { authViewModel.isAdmin() }
    val currentUsername = remember { authViewModel.getUsername() }

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
                    campaigns.value = if (isAdmin) {
                        campaignService.getAllCampaigns()
                    } else {
                        campaignService.getCampaigns()
                    }
                    campaigns.value.forEach { campaign ->
                        println("createdBy: '${campaign.createdBy}' | currentUsername: '$currentUsername'")
                    }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Campanhas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WTCBlue
                )

                if (isAdmin) {
                    Button(
                        onClick = { navController.navigate(Routes.CreateCampaignScreen) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WTCBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Plus,
                            contentDescription = "Criar campanha",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nova",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading.value -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WTCBlue)
                    }
                }

                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 110.dp),
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
                                            campaigns.value = if (isAdmin) {
                                                campaignService.getAllCampaigns()
                                            } else {
                                                campaignService.getCampaigns()
                                            }
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
                        modifier = Modifier.fillMaxSize().padding(bottom = 110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma campanha disponível",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WTCBlue
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(bottom = 125.dp)
                    ) {
                        items(
                            items = campaigns.value,
                            key = { it.id }
                        ) { campaign ->
                            CampaignCard(
                                campaign = campaign,
                                canEdit = campaign.createdBy?.trim()
                                    ?.equals(currentUsername?.trim(), ignoreCase = true) == true,
                                onEditClick = {
                                    navController.navigate(Routes.EditCampaignScreen + "/${campaign.id}")
                                },
                                onDeleteClick = {
                                    coroutineScope.launch {
                                        try {
                                            campaignService.deleteCampaign(campaign.id)
                                            campaigns.value = if (isAdmin) {
                                                campaignService.getAllCampaigns()
                                            } else {
                                                campaignService.getCampaigns()
                                            }
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
    canEdit: Boolean = false,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var subtitleOverflows by remember { mutableStateOf(false) }
    var descriptionOverflows by remember { mutableStateOf(false) }

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
                        color = Color.Gray,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (!expanded) subtitleOverflows = result.hasVisualOverflow
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = campaign.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (!expanded) descriptionOverflows = result.hasVisualOverflow
                        }
                    )

                    if (subtitleOverflows || descriptionOverflows || expanded) {
                        TextButton(
                            onClick = { expanded = !expanded },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = if (expanded) "Ver menos ▲" else "Ver mais ▼",
                                style = MaterialTheme.typography.labelSmall,
                                color = WTCBlue
                            )
                        }
                    }
                }

                if (canEdit) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector =  	FontAwesomeIcons.Solid.Feather,
                                contentDescription = "Editar campanha",
                                tint = WTCBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector =  	FontAwesomeIcons.Solid.Trash,
                                contentDescription = "Deletar campanha",
                                tint = Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

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
                    text = "Por (CRM): ${campaign.createdBy ?: "N/A"}",
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
    }
}