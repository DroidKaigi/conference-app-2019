package io.github.droidkaigi.confsched2019.data.db

import androidx.lifecycle.*
import io.github.droidkaigi.confsched2019.data.db.entity.SessionDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntity
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.LinkedListChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.map
import kotlinx.coroutines.experimental.withContext
import org.threeten.bp.Instant
import javax.inject.Inject

class RoomSessionDatabase @Inject constructor(
        private val databaseSession: SessionCacheDatabase,
        private val sessionDao: SessionDao
//        private val speakerDao: SpeakerDao,
//        private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
//        private val sessionFeedbackDao: SessionFeedbackDao
) : SessionDatabase {
    override fun sessionsChannel(): ReceiveChannel<List<Session>> = sessionDao.sessionsLiveData().observeChannel().map {
        it.orEmpty().map { sessionEntity ->
            sessionEntity.mapSession()
        }
    }

    override suspend fun sessions(): List<Session> {
        return withContext(CommonPool) {
            sessionDao.sessions().map { it.mapSession() }
        }
    }

    fun SessionEntity.mapSession(): Session.SpeechSession {
        return Session.SpeechSession(
                id,
                Instant.ofEpochMilli(stime),
                Instant.ofEpochMilli(etime),
                title,
                desc
        )
    }


    override fun save(sessions: List<Session.SpeechSession>) {
        sessionDao.clearAndInsert(sessions.map { session ->
            SessionEntity(
                    id = session.id,
                    title = session.title,
                    desc = session.desc,
                    stime = session.startTime.toEpochMilli(),
                    etime = session.endTime.toEpochMilli()
            )
        })
    }

}

// from: https://github.com/dmytrodanylyk/coroutines-arch/blob/master/library/src/main/java/com/kotlin/arch/LiveDataChannel.kt
class LiveDataChannel<T>(private val liveData: LiveData<T>)
    : LinkedListChannel<T?>(), ReceiveChannel<T?>, Observer<T?>, LifecycleObserver {

    override fun onChanged(t: T?) {
        offer(t)
    }

    override fun afterClose(cause: Throwable?) = liveData.removeObserver(this)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = close()

}

fun <T> LiveData<T>.observeChannel(lifecycleOwner: LifecycleOwner): LiveDataChannel<T> {
    val channel = LiveDataChannel(this)
    observe(lifecycleOwner, channel)
    lifecycleOwner.lifecycle.addObserver(channel)
    return channel
}

fun <T> LiveData<T>.observeChannel(): LiveDataChannel<T> {
    val channel = LiveDataChannel(this)
    observeForever(channel)
    return channel
}
