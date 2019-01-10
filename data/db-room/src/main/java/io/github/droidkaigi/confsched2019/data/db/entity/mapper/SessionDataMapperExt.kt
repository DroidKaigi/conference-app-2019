package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2019.data.api.response.CategoryItemResponse
import io.github.droidkaigi.confsched2019.data.api.response.CategoryResponse
import io.github.droidkaigi.confsched2019.data.api.response.RoomResponse
import io.github.droidkaigi.confsched2019.data.api.response.SessionResponse
import io.github.droidkaigi.confsched2019.data.api.response.SpeakerResponse
import io.github.droidkaigi.confsched2019.data.db.entity.CategoryEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.LanguageEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.MessageEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.RoomEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntityImpl

fun List<SessionResponse>?.toSessionSpeakerJoinEntities(): List<SessionSpeakerJoinEntityImpl> {
    val sessionSpeakerJoinEntity: MutableList<SessionSpeakerJoinEntityImpl> = arrayListOf()
    this?.forEach { responseSession ->
        responseSession.speakers.forEach { speakerId ->
            sessionSpeakerJoinEntity +=
                SessionSpeakerJoinEntityImpl(responseSession.id, speakerId)
        }
    }
    return sessionSpeakerJoinEntity
}

fun List<SessionResponse>.toSessionEntities(
    categories: List<CategoryResponse>,
    rooms: List<RoomResponse>
): List<SessionEntityImpl> =
    this.map {
        it.toSessionEntityImpl(categories, rooms)
    }

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

private fun parseDateTime(dateText: String): DateTimeTz {
    val tz = "+09:00" // FIXME Should replace to timezone from API
    return dateFormat.parse("$dateText$tz")
}

fun SessionResponse.toSessionEntityImpl(
    categories: List<CategoryResponse>,
    rooms: List<RoomResponse>
): SessionEntityImpl {
    if (!isServiceSession) {
        val sessionFormat = categoryItems[0].let {
            categories.category(0, it)
        }
        val language = categoryItems[1].let {
            categories.category(1, it)
        }
        val category = categoryItems[2].let {
            categories.category(2, it)
        }
        val intendedAudience = questionAnswers[0].answerValue
        return SessionEntityImpl(
            id = id,
            isServiceSession = isServiceSession,
            title = title,
            englishTitle = requireNotNull(englishTitle),
            desc = description,
            stime = parseDateTime(startsAt).utc.unixMillisLong,
            etime = parseDateTime(endsAt).utc.unixMillisLong,
            sessionFormat = requireNotNull(sessionFormat.name),
            language = LanguageEntityImpl(
                requireNotNull(language.id),
                requireNotNull(language.name),
                requireNotNull(language.translatedName?.ja),
                requireNotNull(language.translatedName?.en)
            ),
            message = message?.let {
                MessageEntityImpl(requireNotNull(it.ja), requireNotNull(it.en))
            },
            category = CategoryEntityImpl(
                requireNotNull(category.id),
                requireNotNull(category.name),
                requireNotNull(category.translatedName?.ja),
                requireNotNull(category.translatedName?.en)
            ),
            intendedAudience = intendedAudience,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            room = RoomEntityImpl(roomId, rooms.roomName(roomId)),
            sessionType = sessionType
        )
    } else {
        return SessionEntityImpl(
            id = id,
            isServiceSession = isServiceSession,
            englishTitle = englishTitle,
            title = title,
            desc = description,
            stime = parseDateTime(startsAt).utc.unixMillisLong,
            etime = parseDateTime(endsAt).utc.unixMillisLong,
            sessionFormat = null,
            language = null,
            category = null,
            room = RoomEntityImpl(roomId, rooms.roomName(roomId)),
            intendedAudience = null,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            message = message?.let {
                MessageEntityImpl(requireNotNull(it.ja), requireNotNull(it.en))
            },
            sessionType = sessionType
        )
    }
}

fun List<SpeakerResponse>.toSpeakerEntities(): List<SpeakerEntityImpl> =
    map { responseSpeaker ->
        SpeakerEntityImpl(id = responseSpeaker.id!!,
            name = responseSpeaker.fullName!!,
            tagLine = responseSpeaker.tagLine,
            bio = responseSpeaker.bio,
            imageUrl = responseSpeaker.profilePicture,
            twitterUrl = responseSpeaker.links
                ?.firstOrNull { "Twitter" == it?.linkType }
                ?.url,
            companyUrl = responseSpeaker.links
                ?.firstOrNull { "Company_Website" == it?.linkType }
                ?.url,
            blogUrl = responseSpeaker.links
                ?.firstOrNull { "Blog" == it?.linkType }
                ?.url,
            githubUrl = responseSpeaker.links
                ?.firstOrNull { "GitHub" == it?.title || "Github" == it?.title }
                ?.url
        )
    }

private fun List<CategoryResponse>.category(
    categoryIndex: Int,
    categoryId: Int?
): CategoryItemResponse {
    return this[categoryIndex].items!!.first { it!!.id == categoryId }!!
}

private fun List<RoomResponse>.roomName(roomId: Int?): String = first { it.id == roomId }.name!!
