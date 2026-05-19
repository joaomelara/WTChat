package com.example.wtchat.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.CampaignModel
import com.example.wtchat.ui.theme.WTCBackground
import com.example.wtchat.ui.theme.WTCBlue
import com.example.wtchat.ui.theme.WTCGrey
import com.example.wtchat.ui.theme.WTCOrange
import com.example.wtchat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

private const val MAX_TITLE = 100
private const val MAX_SUBTITLE = 150
private const val MAX_DESCRIPTION = 1000

@Composable
fun EditCampaignScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    campaignId: String
) {
    val campaignService = RetrofitInstance.getInstance().campaignService
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val isAdmin = remember { authViewModel.isAdmin() }

    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    var isLoadingCampaign by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val availableSegments = listOf(
        "RETAIL",
        "HEALTHCARE",
        "EDUCATION",
        "FINANCE",
        "TECHNOLOGY"
    )
    val selectedSegments = remember { mutableStateListOf<String>() }

    LaunchedEffect(campaignId) {
        isLoadingCampaign = true
        try {
            val allCampaigns = if (isAdmin) {
                campaignService.getAllCampaigns()
            } else {
                campaignService.getCampaigns()
            }
            val campaign = allCampaigns.find { it.id == campaignId }
            if (campaign != null) {
                title = campaign.title
                subtitle = campaign.subtitle
                description = campaign.description
                date = campaign.date
                selectedSegments.clear()
                selectedSegments.addAll(
                    campaign.segments.map { it.removePrefix("SEGMENT_") }
                )
            } else {
                errorMessage = "Campanha não encontrada"
            }
        } catch (e: Exception) {
            errorMessage = "Erro ao carregar campanha"
            println("Erro ao carregar campanha: ${e.message}")
        } finally {
            isLoadingCampaign = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WTCBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Voltar",
                        tint = WTCBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Campanha",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WTCBlue
                )
            }

            when {
                isLoadingCampaign -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WTCBlue)
                    }
                }

                errorMessage != null && title.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        errorMessage?.let {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        successMessage?.let {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = it,
                                    color = Color(0xFF388E3C),
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { if (it.length <= MAX_TITLE) title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WTCBlue,
                                focusedLabelColor = WTCBlue,
                                cursorColor = WTCBlue
                            ),
                            singleLine = true,
                            supportingText = {
                                Text(
                                    text = "${title.length}/$MAX_TITLE",
                                    color = if (title.length >= MAX_TITLE) Color.Red else Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { if (it.length <= MAX_SUBTITLE) subtitle = it },
                            label = { Text("Subtítulo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WTCBlue,
                                focusedLabelColor = WTCBlue,
                                cursorColor = WTCBlue
                            ),
                            singleLine = true,
                            supportingText = {
                                Text(
                                    text = "${subtitle.length}/$MAX_SUBTITLE",
                                    color = if (subtitle.length >= MAX_SUBTITLE) Color.Red else Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { if (it.length <= MAX_DESCRIPTION) description = it },
                            label = { Text("Descrição") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WTCBlue,
                                focusedLabelColor = WTCBlue,
                                cursorColor = WTCBlue
                            ),
                            maxLines = 4,
                            supportingText = {
                                Text(
                                    text = "${description.length}/$MAX_DESCRIPTION",
                                    color = if (description.length >= MAX_DESCRIPTION) Color.Red else Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Data (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WTCBlue,
                                focusedLabelColor = WTCBlue,
                                cursorColor = WTCBlue
                            ),
                            singleLine = true
                        )

                        Text(
                            text = "Segmentos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WTCBlue
                        )

                        availableSegments.forEach { segment ->
                            val isSelected = selectedSegments.contains(segment)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedSegments.add(segment)
                                        else selectedSegments.remove(segment)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = WTCBlue,
                                        uncheckedColor = WTCGrey
                                    )
                                )
                                Text(
                                    text = segment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) WTCBlue else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (title.isBlank() || subtitle.isBlank() || description.isBlank() || date.isBlank()) {
                                    errorMessage = "Preencha todos os campos obrigatórios"
                                    return@Button
                                }
                                if (selectedSegments.isEmpty()) {
                                    errorMessage = "Selecione ao menos um segmento"
                                    return@Button
                                }
                                coroutineScope.launch {
                                    isSaving = true
                                    errorMessage = null
                                    successMessage = null
                                    try {
                                        val updatedCampaign = CampaignModel(
                                            id = campaignId,
                                            title = title,
                                            subtitle = subtitle,
                                            description = description,
                                            date = date,
                                            segments = selectedSegments.toList()
                                        )
                                        campaignService.updateCampaign(campaignId, updatedCampaign)
                                        successMessage = "Campanha atualizada com sucesso!"
                                    } catch (e: Exception) {
                                        errorMessage = "Erro ao salvar campanha"
                                        println("Erro ao salvar campanha: ${e.message}")
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WTCBlue),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Salvar alterações",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WTCBlue),
                            enabled = !isSaving
                        ) {
                            Text(
                                text = "Cancelar",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}