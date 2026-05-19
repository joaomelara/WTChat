package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationModel(
    val userId: String = "",
    val annotationText: String = ""
)
