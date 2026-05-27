package com.kfu.itis

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform