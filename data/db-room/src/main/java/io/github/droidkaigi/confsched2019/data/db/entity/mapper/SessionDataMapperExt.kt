package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import com.soywiz.klock.DateTime
import com.soywiz.klock.SimplerDateFormat
import io.github.droidkaigi.confsched2019.data.api.response.*
import io.github.droidkaigi.confsched2019.data.db.entity.*

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

private val FORMATTER: SimplerDateFormat =
    SimplerDateFormat("yyyy-MM-dd'T'HH:mm:ssxxx")

fun SessionResponse.toSessionEntityImpl(categories: List<CategoryResponse>,
    rooms: List<RoomResponse>): SessionEntityImpl {
    val sessionFormat = categories.category(0, categoryItems[0])
    val language = categories.category(1, categoryItems[1])
    val topic = categories.category(2, categoryItems[2])
    val level = categories.category(3, categoryItems[3])
    return SessionEntityImpl(
        id = id,
        title = title,
        desc = description,
        stime = FORMATTER.parse(startsAt),
        etime = FORMATTER.parse(endsAt),
        sessionFormat = sessionFormat.name!!,
        language = language.name!!,
        message = message?.let {
            MessageEntityImpl(it.ja!!, it.en!!)
        },
        topic = TopicEntityImpl(topic.id!!, topic.name!!),
        level = LevelEntityImpl(level.id!!, level.name!!),
        room = RoomEntityImpl(roomId, rooms.roomName(roomId))
    )
}

fun List<SpeakerResponse>.toSpeakerEntities(): List<SpeakerEntityImpl> =
    map { responseSpeaker ->
        SpeakerEntityImpl(id = responseSpeaker.id!!,
            name = responseSpeaker.fullName!!,
            tagLine = responseSpeaker.tagLine!!,
            imageUrl = responseSpeaker.profilePicture.orEmpty(),
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

private fun List<CategoryResponse>.category(categoryIndex: Int, categoryId: Int?): CategoryItemResponse =
    this[categoryIndex].items!!.first { it!!.id == categoryId }!!

private fun List<RoomResponse>.roomName(roomId: Int?): String =
    first { it.id == roomId }.name!!
