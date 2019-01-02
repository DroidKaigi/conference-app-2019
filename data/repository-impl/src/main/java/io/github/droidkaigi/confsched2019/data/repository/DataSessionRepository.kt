package io.github.droidkaigi.confsched2019.data.repository

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.data.repository.mapper.toSession
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DataSessionRepository @Inject constructor(
    private val droidKaigiApi: DroidKaigiApi,
    private val sessionDatabase: SessionDatabase,
    private val fireStore: FireStore
) : SessionRepository {

    override suspend fun sessionContents(): SessionContents = coroutineScope {
        val speakerSessions = speakerSession()
        val sessions = (speakerSessions + Session.SpecialSession.specialSessions())
            .sortedBy { it.startTime }
        SessionContents(
            sessions = sessions,
            speakers = sessions
                .filterIsInstance<Session.SpeechSession>()
                .flatMap { it.speakers }
                .distinct(),
            langs = Lang.values().toList(),
            rooms = speakerSessions.map { it.room }.distinct(),
            topics = speakerSessions.map { it.topic }.distinct()
        )
    }

    private suspend fun CoroutineScope.speakerSession(): List<Session.SpeechSession> {
        val sessionsAsync = async { sessionDatabase.sessions() }
        val allSpeakersAsync = async { sessionDatabase.allSpeaker() }
        val fabSessionIdsAsync = async {
            fireStore.getFavoriteSessionIds()
        }

        val sessionEntities = sessionsAsync.await()
        if (sessionEntities.isEmpty()) return listOf()
        val speakerEntities = allSpeakersAsync.await()
        val fabSessionIds = fabSessionIdsAsync.await()
        val firstDay = DateTime(sessionEntities.first().session.stime)
        val speakerSessions = sessionEntities
            .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
            .sortedWith(compareBy(
                { it.startTime.unixMillisLong },
                { it.room.id }
            ))
        return sessionEntities
            .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
            .sortedWith(compareBy(
                { it.startTime.unixMillisLong },
                { it.room.id }
            ))
    }

    override suspend fun toggleFavorite(session: Session.SpeechSession) {
        fireStore.toggleFavorite(session.id)
    }

    override suspend fun refresh() {
        val response = droidKaigiApi.getSessions()
        sessionDatabase.save(response)
    }
}
