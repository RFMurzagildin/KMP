package com.kfu.itis.util

expect fun todayDateString(): String

fun String.toDisplayDate(): String {
    val parts = split("-")
    return if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else this
}

fun String.toDisplayTime(): String {
    val timePart = substringAfter('T', "")
    return if (timePart.length >= 5) timePart.take(5) else this
}
