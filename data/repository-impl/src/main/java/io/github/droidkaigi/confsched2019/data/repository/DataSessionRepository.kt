package io.github.droidkaigi.confsched2019.data.repository

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
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.openSubscription
import javax.inject.Inject

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

    override suspend fun sessionChannel(): ReceiveChannel<List<Session>> = coroutineScope {
        try {
            val allSpeakerDeferred = async { sessionDatabase.allSpeaker() }
            val fabSessionIdsObservable: Observable<List<Int>> = fireStore
                .getFavoriteSessionChannel()
                .asObservable(Dispatchers.Default)
//            .doOnNext { println("sessionChannel:fabSessionIdsObservable" + it) }
            val sessionsObservable: Observable<List<SessionWithSpeakers>> = sessionDatabase
                .sessionsChannel()
                .asObservable(Dispatchers.Default)
//            .doOnNext { println("sessionChannel:sessionsObservable" + it) }

            val speakerEntities = allSpeakerDeferred.await()
            val observable: Observable<List<Session>> = Observable
                .combineLatest(
                    fabSessionIdsObservable,
                    sessionsObservable,
                    BiFunction<List<Int>, List<SessionWithSpeakers>, List<Session>> { fabSessionIds, sessionEntities: List<SessionWithSpeakers> ->
                        val firstDay = DateTime(sessionEntities.first().session.stime)
                        val speakerSessions = sessionEntities
                            .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
                            .sortedWith(compareBy(
                                { it.startTime.unix },
                                { it.room.id }
                            ))
                        speakerSessions // + specialSessions
                    }
                )
            val receiveChannel: ReceiveChannel<List<Session>> = observable
                .openSubscription()
            return@coroutineScope receiveChannel
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
