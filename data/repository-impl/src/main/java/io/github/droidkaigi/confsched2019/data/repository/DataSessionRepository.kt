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
        val sessions = sessions()
            .sortedBy { it.startTime }
        val speechSessions = sessions.filterIsInstance<Session.SpeechSession>()
        SessionContents(
            sessions = sessions,
            speakers = speechSessions.flatMap { it.speakers }.distinct(),
            langs = Lang.values().toList(),
            rooms = speechSessions.map { it.room }.distinct(),
            category = speechSessions.map { it.category }.distinct()
        )
    }

    private suspend fun CoroutineScope.sessions(): List<Session> {
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
