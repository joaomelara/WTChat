package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val id: String,
    val email: String,
    val username: String,
    val roles: List<String>,
    val segment: String
)

@Serializable
data class SignUpRequest(
    val crm: String,
    val username: String,
    val email: String,
    val password: String
)

