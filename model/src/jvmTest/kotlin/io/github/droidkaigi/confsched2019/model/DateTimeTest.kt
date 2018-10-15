package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.SimplerDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {

    @Test fun dateFormat() {
        assertEquals(16, SimplerDateFormat("""yyyy-MM-dd'T'HH:mm:ss""")
            .parseDate("2018-10-08T16:32:59")
            .hours)
    }
}
