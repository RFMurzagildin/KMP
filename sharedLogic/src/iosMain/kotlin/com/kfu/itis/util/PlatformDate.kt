package com.kfu.itis.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual fun todayDateString(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    formatter.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    return formatter.stringFromDate(NSDate())
}
