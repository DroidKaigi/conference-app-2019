package io.github.droidkaigi.confsched2019.data.repository.mapper

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionMessage
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.Category

fun SessionWithSpeakers.toSession(
    speakerEntities: List<SpeakerEntity>,
    favList: List<Int>?,
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
            room = Room(session.room.id, session.room.name)
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
            title = session.title,
            desc = session.desc,
            room = Room(session.room.id, session.room.name),
            format = session.sessionFormat,
            language = session.language,
            category = Category(session.category.id, session.category.name),
            intendedAudience = session.intendedAudience,
            isFavorited = favList!!.map { it.toString() }.contains(session.id),
            speakers = speakers,
            message = session.message?.let {
                SessionMessage(it.ja, it.en)
            }
        )
    }
}

fun SpeakerEntity.toSpeaker(): Speaker = Speaker(
    id = id,
    name = name,
    tagLine = tagLine,
    imageUrl = imageUrl,
    twitterUrl = twitterUrl,
    companyUrl = companyUrl,
    blogUrl = blogUrl,
    githubUrl = githubUrl
)
