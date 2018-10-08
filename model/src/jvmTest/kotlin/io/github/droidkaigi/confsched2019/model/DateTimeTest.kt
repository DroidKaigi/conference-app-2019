package io.github.droidkaigi.confsched2019.model

import kotlin.test.*
import com.soywiz.klock.*

class DateTimeTest {

    @Test
    fun dateFormat() {
        assertEquals(16, SimplerDateFormat("""yyyy-MM-dd'T'HH:mm:ss""")
            .parseDate("2018-10-08T16:32:59")
            .hours)

    }
}
