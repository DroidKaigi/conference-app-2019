package io.github.droidkaigi.confsched2019.data.repository

import android.util.Log
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.model.Level
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionMessage
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.Topic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class DataSessionRepository @Inject constructor(
    val sessionApi: SessionApi,
    val sessionDatabase: SessionDatabase,
    val fireStore: FireStore
) : SessionRepository {
    override suspend fun sessions(withFavorite: Boolean): List<Session> = coroutineScope {
        val sessionsAsync = async { sessionDatabase.sessions() }
        val allSpeakersAsync = async { sessionDatabase.allSpeaker() }
        val fabSessionIdsAsync = async {
            if (withFavorite) fireStore.getFavoriteSessionIds() else listOf()
        }

        awaitAll(fabSessionIdsAsync, sessionsAsync, allSpeakersAsync)
        val sessionEntities = sessionsAsync.await()
        if (sessionEntities.isEmpty()) return@coroutineScope listOf<Session>()
        val speakerEntities = allSpeakersAsync.await()
        val fabSessionIds = fabSessionIdsAsync.await()
        val firstDay = DateTime(sessionEntities.first().session.stime)
        val speakerSessions = sessionEntities
            .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
            .sortedWith(compareBy(
                { it.startTime.unix },
                { it.room.id }
            ))
        speakerSessions //  + specialSessions
    }

    override suspend fun sessionChannel(): ReceiveChannel<List<Session>> {
        try {
            val allSpeakerDeferred = async { sessionDatabase.allSpeaker() }
            val fabSessionIdsChannel: ReceiveChannel<List<Int>> = fireStore
                .getFavoriteSessionChannel()
//            .doOnNext { println("sessionChannel:fabSessionIdsObservable" + it) }
            val sessionsChannel: ReceiveChannel<List<SessionWithSpeakers>> = sessionDatabase
                .sessionsChannel()
//            .doOnNext { println("sessionChannel:sessionsObservable" + it) }
            val speakerEntities = allSpeakerDeferred.await()

            val channel: BroadcastChannel<List<Session>> = BroadcastChannel<List<Session>>(
                Channel.CONFLATED)

            CoroutineScope(coroutineContext).launch {
                var fabSessions: List<Int>? = null
                var sessionEntities: List<SessionWithSpeakers>? = null
                try {
                    while (true) {
                        select<Unit> {
                            fabSessionIdsChannel.onReceive {
                                fabSessions = it
                            }
                            sessionsChannel.onReceive {
                                sessionEntities = it
                            }
                            val nonNullFabSessions = fabSessions ?: return@select
                            val nonNullSessionEntities = sessionEntities ?: return@select
                            val firstDay = DateTime(nonNullSessionEntities.first().session.stime)
                            val speakerSessions = nonNullSessionEntities
                                .map { it.toSession(speakerEntities, nonNullFabSessions, firstDay) }
                                .sortedWith(compareBy(
                                    { it.startTime.unix },
                                    { it.room.id }
                                ))
                            channel.offer(speakerSessions) // + specialSessions
                        }
                    }
                } finally {
                    Log.d("DataSessionRepository", "finally")
                    sessionsChannel.cancel()
                    fabSessionIdsChannel.cancel()
                    channel.close()
                }
            }

            return channel.openSubscription()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun toggleFavorite(session: Session.SpeechSession) {
        fireStore.toggleFavorite(session.id)
    }

    // TODO: separaete mapper
    fun SessionWithSpeakers.toSession(
        speakerEntities: List<SpeakerEntity>,
        favList: List<Int>?,
        firstDay: DateTime
    ): Session.SpeechSession {
        val sessionEntity = session
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
            startTime = com.soywiz.klock.DateTime.fromUnix(sessionEntity.stime),
            endTime = com.soywiz.klock.DateTime.fromUnix(sessionEntity.etime),
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
