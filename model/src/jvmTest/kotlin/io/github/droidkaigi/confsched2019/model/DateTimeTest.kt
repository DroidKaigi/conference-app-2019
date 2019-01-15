package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.soywiz.klock.parse
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {

    private val dateFormat = DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    private val eventTimeString = "2019-02-07T08:12:34+09:00"
    private val jstOffset = 9.hours

    @Test fun dateFormat() {
        val parsedTimeUnixMillis = dateFormat.parse(eventTimeString).utc.unixMillisLong
        val parsedTime: DateTime = DateTime.fromUnix(parsedTimeUnixMillis)
        val parsedTimeTZ: DateTimeTz = parsedTime.toOffset(jstOffset)

        assertEquals(2019, parsedTime.yearInt)
        assertEquals(2, parsedTime.month1)
        assertEquals(6, parsedTime.dayOfMonth)
        assertEquals(23, parsedTime.hours)
        assertEquals(12, parsedTime.minutes)
        assertEquals(34, parsedTime.seconds)

        assertEquals(2019, parsedTimeTZ.yearInt)
        assertEquals(2, parsedTimeTZ.month1)
        assertEquals(7, parsedTimeTZ.dayOfMonth)
        assertEquals(8, parsedTimeTZ.hours)
        assertEquals(12, parsedTimeTZ.minutes)
        assertEquals(34, parsedTimeTZ.seconds)
        assertEquals(9.0, parsedTimeTZ.offset.time.hours)

        assertEquals(37, parsedTime.dayOfYear)
        assertEquals(38, parsedTimeTZ.dayOfYear)

        assertEquals(parsedTime.unixMillisLong, parsedTimeTZ.utc.unixMillisLong)
    }

    @Test fun createAdjusted() {
        val sessionTime = DateTime.createAdjusted(2019, 2, 7, 8, 12, 34)
        val sessionTimeTZ = sessionTime.toOffsetUnadjusted(jstOffset)

        assertEquals(2019, sessionTime.yearInt)
        assertEquals(2, sessionTime.month1)
        assertEquals(7, sessionTime.dayOfMonth)
        assertEquals(8, sessionTime.hours)
        assertEquals(12, sessionTime.minutes)
        assertEquals(34, sessionTime.seconds)

        assertEquals(2019, sessionTimeTZ.yearInt)
        assertEquals(2, sessionTimeTZ.month1)
        assertEquals(7, sessionTimeTZ.dayOfMonth)
        assertEquals(8, sessionTimeTZ.hours)
        assertEquals(12, sessionTimeTZ.minutes)
        assertEquals(34, sessionTimeTZ.seconds)
        assertEquals(9.0, sessionTimeTZ.offset.time.hours)

        assertEquals(sessionTime.minus(jstOffset).unixMillisLong, sessionTimeTZ.utc.unixMillisLong)

        val parsedTimeUnixMillis = dateFormat.parse(eventTimeString).utc.unixMillisLong
        assertEquals(parsedTimeUnixMillis, sessionTimeTZ.utc.unixMillisLong)
    }
}
