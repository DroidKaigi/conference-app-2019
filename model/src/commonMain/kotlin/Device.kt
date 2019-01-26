package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.DateTimeTz

class Device {
    companion object {
        fun getOffsetFromUTC(): DateTimeSpan {
            val nowLocal = DateTimeTz.nowLocal()
            val offsetHours = nowLocal.utc.localOffset.time.hours.toInt()
            val offsetMinutes = nowLocal.utc.localOffset.time.minutes.toInt() - (offsetHours * 60)
            return DateTimeSpan(hours = offsetHours, minutes = offsetMinutes)
        }
    }
}
