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
import com.example.wtchat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateCampaignScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val campaignService = RetrofitInstance.getInstance().campaignService
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

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
                    text = "Nova Campanha",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WTCBlue
                )
            }

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
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WTCBlue,
                        focusedLabelColor = WTCBlue,
                        cursorColor = WTCBlue
                    ),
                    singleLine = true
                )

                // Campo Subtítulo
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtítulo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WTCBlue,
                        focusedLabelColor = WTCBlue,
                        cursorColor = WTCBlue
                    ),
                    singleLine = true
                )

                // Campo Descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
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
                    maxLines = 4
                )

                // Campo Data
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

                // Seleção de Segmentos
                Text(
                    text = "Segmentos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WTCBlue
                )

                availableSegments.forEach { segment ->
                    val isSelected = selectedSegments.contains(segment)
                    val label = segment.removePrefix("SEGMENT_")

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
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) WTCBlue else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botão Criar
                Button(
                    onClick = {
                        if (title.isBlank() || subtitle.isBlank() || description.isBlank() || date.isBlank()) {
                            errorMessage = "Preencha todos os campos obrigatórios"
                            return@Button
                        }
                        coroutineScope.launch {
                            isSaving = true
                            errorMessage = null
                            successMessage = null
                            try {
                                val newCampaign = CampaignModel(
                                    title = title,
                                    subtitle = subtitle,
                                    description = description,
                                    date = date,
                                    segments = selectedSegments.toList()
                                )
                                campaignService.createCampaign(newCampaign)
                                successMessage = "Campanha criada com sucesso!"
                                // Limpa o formulário após criar
                                title = ""
                                subtitle = ""
                                description = ""
                                date = ""
                                selectedSegments.clear()
                            } catch (e: Exception) {
                                errorMessage = "Erro ao criar campanha"
                                println("Erro ao criar campanha: ${e.message}")
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
                            text = "Criar campanha",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botão Cancelar
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