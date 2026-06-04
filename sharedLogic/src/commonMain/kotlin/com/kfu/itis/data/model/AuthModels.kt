package com.kfu.itis.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwtRequest(
    val username: String,
    val password: String,
)

@Serializable
data class RegistrationUserDto(
    val username: String,
    val password: String,
    @SerialName("confirmPassword")
    val confirmPassword: String,
)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
)
