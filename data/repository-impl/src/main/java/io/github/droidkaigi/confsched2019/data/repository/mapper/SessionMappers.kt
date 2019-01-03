package io.github.droidkaigi.confsched2019.data.repository.mapper

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionMessage
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.Topic

fun SessionWithSpeakers.toSession(
    speakerEntities: List<SpeakerEntity>,
    favList: List<Int>?,
    firstDay: DateTime
): Session {
    val sessionEntity = session
    if (sessionEntity.isServiceSession) {
        return Session.ServiceSession(
            id = sessionEntity.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(sessionEntity.stime).dayOfYear - firstDay.dayOfYear + 1,
            startTime = DateTime.fromUnix(sessionEntity.stime),
            endTime = DateTime.fromUnix(sessionEntity.etime),
            title = sessionEntity.title,
            room = Room(sessionEntity.room.id, sessionEntity.room.name)
        )
    } else {
        require(speakerIdList.isNotEmpty())
        val speakers = speakerIdList.map { speakerId ->
            val speakerEntity = speakerEntities.first { it.id == speakerId }
            speakerEntity.toSpeaker()
        }
        require(speakers.isNotEmpty())
        return Session.SpeechSession(
            id = sessionEntity.id,
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(sessionEntity.stime).dayOfYear - firstDay.dayOfYear + 1,
            startTime = DateTime.fromUnix(sessionEntity.stime),
            endTime = DateTime.fromUnix(sessionEntity.etime),
            title = sessionEntity.title,
            desc = sessionEntity.desc,
            room = Room(sessionEntity.room.id, sessionEntity.room.name),
            format = sessionEntity.sessionFormat,
            language = sessionEntity.language,
            topic = Topic(sessionEntity.topic.id, sessionEntity.topic.name),
            isFavorited = favList!!.map { it.toString() }.contains(sessionEntity.id),
            speakers = speakers,
            message = sessionEntity.message?.let {
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
