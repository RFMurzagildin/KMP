package com.kfu.itis.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long? = null,
    val description: String,
    val time: String,
)
