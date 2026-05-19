package com.example.wtchat.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(

    val accessToken: String,
    val id: String,
    val username: String,
    val name: String,
    val email: String,
    val roles: List<String>,
    val segment: String,

)

@Serializable
data class SignUpRequest(
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val segment: String,
    val roles: Set<String>? = null
)