package io.github.droidkaigi.confsched2019.ext.android

import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.DateTimeTz

data class Offset(val hours: Int, val minute: Int)


fun DateTimeTz.offsetTimeSpanFromUTC(): DateTimeSpan {
    val offsetHours = hours - DateTimeTz.nowLocal().utc.hours
    val offsetMinutes = minutes - DateTimeTz.nowLocal().utc.minutes
    return DateTimeSpan(hours = offsetHours, minutes = offsetMinutes)
}


