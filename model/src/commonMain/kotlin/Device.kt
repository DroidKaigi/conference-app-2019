package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.DateTimeTz

class Device {
    companion object {
        fun getOffsetFromUTC() : DateTimeSpan{
            val nowLocal = DateTimeTz.nowLocal()
            val offsetHours = nowLocal.hours - nowLocal.utc.hours
            val offsetMinutes = nowLocal.minutes - nowLocal.utc.minutes
            return DateTimeSpan(hours = offsetHours, minutes = offsetMinutes)
        }
    }
}
