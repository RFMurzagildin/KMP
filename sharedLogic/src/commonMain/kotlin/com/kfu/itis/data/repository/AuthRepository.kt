package com.kfu.itis.data.repository

import com.kfu.itis.data.model.ApiResponse
import com.kfu.itis.data.model.JwtRequest
import com.kfu.itis.data.model.RegistrationUserDto
import com.kfu.itis.data.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepository {
    private val client = ApiClient.http
    private val baseUrl = ApiClient.BASE_URL

    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val apiResponse = client.post("$baseUrl/auth") {
                contentType(ContentType.Application.Json)
                setBody(JwtRequest(username, password))
            }.body<ApiResponse>()

            if (apiResponse.success && apiResponse.message != null) {
                Result.success(apiResponse.message)
            } else {
                Result.failure(Exception(apiResponse.message ?: "Неверный логин или пароль"))
            }
        } catch (e: ResponseException) {
            val message = when (e.response.status.value) {
                400 -> "Неверный запрос"
                401 -> "Неверный логин или пароль"
                else -> "Ошибка сервера: ${e.response.status.value}"
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
    ): Result<ApiResponse> {
        return try {
            val apiResponse = client.post("$baseUrl/registration") {
                contentType(ContentType.Application.Json)
                setBody(RegistrationUserDto(username, password, confirmPassword))
            }.body<ApiResponse>()

            if (apiResponse.success) {
                Result.success(apiResponse)
            } else {
                Result.failure(Exception(apiResponse.message ?: "Ошибка регистрации"))
            }
        } catch (e: ResponseException) {
            val message = when (e.response.status.value) {
                400 -> "Неверные данные регистрации"
                409 -> "Пользователь с таким именем уже существует"
                else -> "Ошибка сервера: ${e.response.status.value}"
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }
}
