package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {

    @Test fun dateFormat() {
        assertEquals(
            16, DateFormat("""yyyy-MM-dd'T'HH:mm:ss""")
            .parse("2018-10-08T16:32:59")
            .utc
            .hours
        )
    }
}
