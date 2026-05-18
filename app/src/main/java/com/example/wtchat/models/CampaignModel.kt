package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class CampaignModel(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val date: String = "",
    val createdBy: String = "",
    val segments: List<String> = emptyList()
)

