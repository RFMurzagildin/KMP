package com.kfu.itis.session

object SessionManager {
    var authToken: String? = null

    val isAuthenticated: Boolean
        get() = authToken != null

    fun logout() {
        authToken = null
    }
}
