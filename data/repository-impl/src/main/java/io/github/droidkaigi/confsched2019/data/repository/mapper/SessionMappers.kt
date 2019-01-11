package io.github.droidkaigi.confsched2019.data.repository.mapper

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionType
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.LocaledString

fun SessionWithSpeakers.toSession(
    speakerEntities: List<SpeakerEntity>,
    favList: List<String>?,
    firstDay: DateTime
): Session {
    return if (session.isServiceSession) {
        Session.ServiceSession(
            id = session.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(session.stime).dayOfYear - firstDay.dayOfYear + 1,
            startTime = DateTime.fromUnix(session.stime),
            endTime = DateTime.fromUnix(session.etime),
            title = session.title,
            room = requireNotNull(session.room).let { room ->
                Room(room.id, room.name)
            },
            sessionType = SessionType.of(session.sessionType),
            isFavorited = favList!!.contains(session.id)
        )
    } else {
        require(speakerIdList.isNotEmpty())
        val speakers = speakerIdList.map { speakerId ->
            val speakerEntity = speakerEntities.first { it.id == speakerId }
            speakerEntity.toSpeaker()
        }
        require(speakers.isNotEmpty())
        Session.SpeechSession(
            id = session.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(session.stime).dayOfYear - firstDay.dayOfYear + 1,
            startTime = DateTime.fromUnix(session.stime),
            endTime = DateTime.fromUnix(session.etime),
            title = LocaledString(session.title, requireNotNull(session.englishTitle)),
            desc = session.desc,
            room = Room(requireNotNull(session.room?.id), requireNotNull(session.room?.name)),
            format = requireNotNull(session.sessionFormat),
            language = requireNotNull(session.language).let { language ->
                LocaledString(
                    language.jaName,
                    language.enName
                )
            },
            category = requireNotNull(session.category).let { category ->
                Category(
                    category.id,
                    LocaledString(
                        ja = category.jaName,
                        en = category.enName
                    )
                )
            },
            intendedAudience = session.intendedAudience,
            videoUrl = session.videoUrl,
            slideUrl = session.slideUrl,
            isInterpretationTarget = session.isInterpretationTarget,
            isFavorited = favList!!.contains(session.id),
            speakers = speakers,
            message = session.message?.let {
                LocaledString(it.ja, it.en)
            }
        )
    }
}

fun SpeakerEntity.toSpeaker(): Speaker = Speaker(
    id = id,
    name = name,
    tagLine = tagLine,
    bio = bio,
    imageUrl = imageUrl,
    twitterUrl = twitterUrl,
    companyUrl = companyUrl,
    blogUrl = blogUrl,
    githubUrl = githubUrl
)
