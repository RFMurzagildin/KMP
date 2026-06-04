package com.kfu.itis.data.repository

import com.kfu.itis.data.model.ApiResponse
import com.kfu.itis.data.model.TaskDto
import com.kfu.itis.data.network.ApiClient
import com.kfu.itis.session.SessionManager
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TaskRepository {
    private val client = ApiClient.http
    private val baseUrl = ApiClient.BASE_URL

    private val authHeader: String
        get() = "Bearer ${SessionManager.authToken}"

    suspend fun getTasksByDate(date: String): Result<List<TaskDto>> {
        return try {
            val tasks = client.get("$baseUrl/tasks/get-user-tasks-by-date") {
                header("Authorization", authHeader)
                parameter("date", date)
            }.body<List<TaskDto>>()
            Result.success(tasks)
        } catch (e: ResponseException) {
            Result.failure(Exception("Ошибка сервера: ${e.response.status.value}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun getAllTasks(): Result<List<TaskDto>> {
        return try {
            val tasks = client.get("$baseUrl/tasks/get-user-tasks") {
                header("Authorization", authHeader)
            }.body<List<TaskDto>>()
            Result.success(tasks)
        } catch (e: ResponseException) {
            Result.failure(Exception("Ошибка сервера: ${e.response.status.value}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun createTask(task: TaskDto): Result<Unit> {
        return try {
            val response = client.post("$baseUrl/tasks/new-task") {
                header("Authorization", authHeader)
                contentType(ContentType.Application.Json)
                setBody(task)
            }.body<ApiResponse>()
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message ?: "Не удалось создать задачу"))
        } catch (e: ResponseException) {
            Result.failure(Exception("Ошибка сервера: ${e.response.status.value}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun deleteTask(taskId: Long): Result<Unit> {
        return try {
            val response = client.post("$baseUrl/tasks/delete-task/$taskId") {
                header("Authorization", authHeader)
            }.body<ApiResponse>()
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message ?: "Не удалось удалить задачу"))
        } catch (e: ResponseException) {
            Result.failure(Exception("Ошибка сервера: ${e.response.status.value}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun countTasksByDate(date: String): Result<Long> {
        return try {
            val result = client.get("$baseUrl/tasks/count-user-tasks-by-date") {
                header("Authorization", authHeader)
                parameter("date", date)
            }.body<Map<String, Long>>()
            Result.success(result["count"] ?: 0L)
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка: ${e.message}"))
        }
    }
}
