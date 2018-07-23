package io.github.droidkaigi.confsched2019.session.data.db

import androidx.lifecycle.*
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionDao
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionEntity
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.LinkedListChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.map
import org.threeten.bp.Instant
import javax.inject.Inject

class RoomSessionDatabase @Inject constructor(
        private val database: AppDatabase,
        private val sessionDao: SessionDao
//        private val speakerDao: SpeakerDao,
//        private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
//        private val sessionFeedbackDao: SessionFeedbackDao
) : SessionDatabase {
    override fun getAllSessions(): ReceiveChannel<List<Session>> = sessionDao.getAllSessions().observeChannel().map {
        it.orEmpty().map { sessionEntity ->
            Session.SpeechSession(
                    sessionEntity.id,
                    Instant.ofEpochMilli(sessionEntity.stime),
                    Instant.ofEpochMilli(sessionEntity.etime),
                    sessionEntity.title,
                    sessionEntity.desc
            )
        }
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