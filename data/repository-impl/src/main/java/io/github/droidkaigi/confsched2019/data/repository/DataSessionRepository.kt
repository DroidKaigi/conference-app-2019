package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.session.model.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.coroutineScope
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import javax.inject.Inject

class DataSessionRepository @Inject constructor(
        val sessionApi: SessionApi,
        val sessionDatabase: SessionDatabase,
        val fireStore: FireStore
) : SessionRepository {
    override suspend fun sessions(): List<Session> = coroutineScope {
        val fabSessionIdsAsync = async { fireStore.getFavoriteSessionIds() }
        val sessionsAsync = async { sessionDatabase.sessions() }
        val allSpeakersAsync = async { sessionDatabase.allSpeaker() }

        awaitAll(fabSessionIdsAsync, sessionsAsync, allSpeakersAsync)
        val sessionEntities = sessionsAsync.await()
        val speakerEntities = allSpeakersAsync.await()
        val fabSessionIds = fabSessionIdsAsync.await()
        val firstDay = Instant.ofEpochMilli(sessionEntities.first().session.stime).atZone(ZoneId.of("JST", ZoneId.SHORT_IDS)).toLocalDate()
        val speakerSessions = sessionEntities
                .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
                .sortedWith(compareBy(
                        { it.startTime.epochSecond },
                        { it.room.id }
                ))

        speakerSessions//  + specialSessions
    }

    override suspend fun toggleFavorite(session: Session.SpeechSession) {
        fireStore.toggleFavorite(session.id)
    }

    // TODO: separaete mapper
    fun SessionWithSpeakers.toSession(
            speakerEntities: List<SpeakerEntity>,
            favList: List<Int>?,
            firstDay: LocalDate
    ): Session.SpeechSession {
        val sessionEntity = session
        kotlin.require(speakerIdList.isNotEmpty())
        val speakers = speakerIdList.map { speakerId ->
            val speakerEntity = speakerEntities.first { it.id == speakerId }
            speakerEntity.toSpeaker()
        }
        kotlin.require(speakers.isNotEmpty())
        return Session.SpeechSession(
                id = sessionEntity.id,
                // dayNumber is starts with 1. Example: First day = 1, Second day = 2. So I plus 1 to period days
                dayNumber = org.threeten.bp.Period.between(
                        firstDay, Instant.ofEpochMilli(sessionEntity.stime).atZone(ZoneId.of("JST", ZoneId.SHORT_IDS)).toLocalDate()).days + 1,
                startTime = Instant.ofEpochMilli(sessionEntity.stime),
                endTime = Instant.ofEpochMilli(sessionEntity.etime),
                title = sessionEntity.title,
                desc = sessionEntity.desc,
                room = Room(sessionEntity.room.id, sessionEntity.room.name),
                format = sessionEntity.sessionFormat,
                language = sessionEntity.language,
                topic = Topic(sessionEntity.topic.id, sessionEntity.topic.name),
                level = Level.of(sessionEntity.level.id, sessionEntity.level.name),
                isFavorited = favList!!.map { it.toString() }.contains(sessionEntity.id),
                speakers = speakers,
                message = sessionEntity.message?.let {
                    SessionMessage(it.ja, it.en)
                }
        )
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

    override suspend fun refresh() {
        val response = sessionApi.getSessions()
        sessionDatabase.save(response)
    }
}
