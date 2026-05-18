package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class CampaignModel(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val date: String = "",
    val createdBy: String? = null,
    val segments: List<String> = emptyList()
)

@Serializable
data class DateModel(
    val year: Int = 0,
    val monthValue: Int = 0,
    val dayOfMonth: Int = 0
) {
    override fun toString(): String =
        "%02d/%02d/%04d".format(dayOfMonth, monthValue, year)
}