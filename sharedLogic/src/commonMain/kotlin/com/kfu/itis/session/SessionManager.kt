package com.kfu.itis.session

object SessionManager {
    var authToken: String? = null

    fun logout() {
        authToken = null
    }
}
