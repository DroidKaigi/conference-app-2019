package io.github.droidkaigi.confsched2019.data.repository

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2019.data.api.response.CategoryItemResponse
import io.github.droidkaigi.confsched2019.data.api.response.CategoryResponse
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.RoomResponse
import io.github.droidkaigi.confsched2019.data.api.response.SessionResponse
import io.github.droidkaigi.confsched2019.data.api.response.SpeakerResponse
import io.github.droidkaigi.confsched2019.model.AudienceCategory
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LangSupport
import io.github.droidkaigi.confsched2019.model.LocaledString
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionType
import io.github.droidkaigi.confsched2019.model.Speaker

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

fun Response.toModel(): SessionContents {
    val firstDay = dateFormat.parse(sessions.first().startsAtWithTZ)

    val sessions = sessions.map {
        it.toSession(
            firstDay,
            requireNotNull(speakers),
            requireNotNull(rooms),
            requireNotNull(categories)
        )
    }
    val speechSessions = sessions.filterIsInstance<Session.SpeechSession>()
    return SessionContents(
        sessions = sessions,
        speakers = speechSessions.flatMap { it.speakers }.distinct(),
        langs = Lang.values().toList(),
        langSupports = LangSupport.values().toList(),
        rooms = sessions.map { it.room }.sortedBy { it.name }.distinct(),
        category = speechSessions.map { it.category }.distinct(),
        audienceCategories = AudienceCategory.values().toList()
    )
}

private fun SessionResponse.toSession(
    firstDay: DateTimeTz,
    speakers: List<SpeakerResponse>,
    rooms: List<RoomResponse>,
    categoryResponse: List<CategoryResponse>
): Session {
    val response = this
    val startTime = dateFormat.parse(response.startsAtWithTZ)
    val endTime = dateFormat.parse(response.endsAtWithTZ)
    val room = rooms.first { response.roomId == it.id }

    return if (!response.isServiceSession) {
        val sessionFormat = categoryItems[0].let {
            categoryResponse.category(0, it)
        }
        val language = categoryItems[1].let {
            categoryResponse.category(1, it)
        }
        val category = categoryItems[2].let {
            categoryResponse.category(2, it)
        }
        Session.SpeechSession(
            id = response.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = startTime.dayOfYear - firstDay.dayOfYear + 1,
            startTime = startTime.utc,
            endTime = endTime.utc,
            title = LocaledString(response.title, requireNotNull(response.englishTitle)),
            desc = response.description,
            room = Room(response.roomId, requireNotNull(room.name)),
            format = requireNotNull(sessionFormat.name),
            lang = Lang.findLang(requireNotNull(language.name)),
            category = Category(
                requireNotNull(category.id),
                LocaledString(
                    ja = requireNotNull(category.translatedName?.ja),
                    en = requireNotNull(category.translatedName?.en)
                )
            ),
            intendedAudience = questionAnswers[0].answerValue,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            // TODO
            isFavorited = false,
            speakers = response.speakers
                .map { speakerId -> speakers.first { speakerId == it.id } }
                .map {
                    Speaker(
                        id = requireNotNull(it.id),
                        name = requireNotNull(it.fullName),
                        bio = it.bio,
                        tagLine = it.tagLine,
                        imageUrl = it.profilePicture,
                        twitterUrl = it.links
                            ?.firstOrNull { "Twitter" == it?.linkType }
                            ?.url,
                        companyUrl = it.links
                            ?.firstOrNull { "Company_Website" == it?.linkType }
                            ?.url,
                        blogUrl = it.links
                            ?.firstOrNull { "Blog" == it?.linkType }
                            ?.url,
                        githubUrl = it.links
                            ?.firstOrNull { "GitHub" == it?.title || "Github" == it?.title }
                            ?.url
                    )
                },
            message = response.message?.let {
                LocaledString(
                    requireNotNull(it.ja),
                    requireNotNull(it.en)
                )
            },
            forBeginners = response.forBeginners
        )
    } else {
        Session.ServiceSession(
            id = response.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = startTime.dayOfYear - firstDay.dayOfYear + 1,
            startTime = startTime.utc,
            endTime = endTime.utc,
            title = LocaledString(response.title, requireNotNull(response.englishTitle)),
            desc = response.description,
            room = Room(response.roomId, requireNotNull(room.name)),
            // TODO
            isFavorited = false,
            sessionType = SessionType.of(response.sessionType)
        )
    }
}

private fun List<CategoryResponse>.category(
    categoryIndex: Int,
    categoryId: Int?
): CategoryItemResponse {
    return this[categoryIndex].items!!.first { it!!.id == categoryId }!!
}
