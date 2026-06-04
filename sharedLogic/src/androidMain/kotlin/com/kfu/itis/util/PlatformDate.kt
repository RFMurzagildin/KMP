package com.kfu.itis.util

actual fun todayDateString(): String = java.time.LocalDate.now().toString()
