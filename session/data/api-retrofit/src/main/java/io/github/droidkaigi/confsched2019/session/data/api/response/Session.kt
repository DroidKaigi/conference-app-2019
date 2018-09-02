package io.github.droidkaigi.confsched2019.session.data.api.response

import kotlinx.serialization.*
import kotlinx.serialization.internal.PrimitiveDesc
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Serializable
data class Session(
        val id: String,
        val isServiceSession: Boolean,
        val isPlenumSession: Boolean,
        val speakers: List<String>,
        val description: String,
        val startsAt: Instant,
        val title: String,
        val endsAt: Instant,
        val roomId: Int,
        val categoryItems: List<Int>
)

@Serializer(Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val serialClassDesc: KSerialClassDesc
        get() = PrimitiveDesc("org.threeten.bp.Instant")

    override fun load(input: KInput): Instant = parseDateString(input.readStringValue())

    override fun save(output: KOutput, obj: Instant) {
        output.writeStringValue(obj.atJST().format(FORMATTER))
    }

    private val FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    /**
     * Obtains an instance of Instant from a text string such as "2018-02-08T17:40:00".
     * The string must represent in JST (GMT+9:00).
     */
    fun parseDateString(dateString: String): Instant =
            LocalDateTime.parse(dateString, FORMATTER).atJST().toInstant()
}

fun Instant.atJST(): ZonedDateTime {
    return atZone(ZoneId.of("JST", ZoneId.SHORT_IDS))
}

fun LocalDateTime.atJST(): ZonedDateTime {
    return atZone(ZoneId.of("JST", ZoneId.SHORT_IDS))
}