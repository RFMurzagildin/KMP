package com.kfu.itis.util

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isLeapYear(year)) 29 else 28
    else -> 30
}

fun firstDayOfMonth(year: Int, month: Int): Int {
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    val y = if (month < 3) year - 1 else year
    val dow = (y + y / 4 - y / 100 + y / 400 + t[month - 1] + 1) % 7  // 0=Sunday
    return (dow + 6) % 7  // shift to 0=Monday
}

val MONTH_NAMES_RU = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)

fun monthName(month: Int): String = MONTH_NAMES_RU[month - 1]

fun formatDateString(year: Int, month: Int, day: Int): String =
    "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

fun subtractDays(dateString: String, days: Int): String {
    var y = dateString.substring(0, 4).toInt()
    var m = dateString.substring(5, 7).toInt()
    var d = dateString.substring(8, 10).toInt()
    var remaining = days
    while (remaining > 0) {
        d--
        if (d == 0) {
            m--
            if (m == 0) { m = 12; y-- }
            d = daysInMonth(y, m)
        }
        remaining--
    }
    return formatDateString(y, m, d)
}

fun getDayOfWeekShort(year: Int, month: Int, day: Int): String {
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    val y = if (month < 3) year - 1 else year
    val dow = (y + y / 4 - y / 100 + y / 400 + t[month - 1] + day) % 7
    return listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")[(dow + 6) % 7]
}
