package io.github.droidkaigi.confsched2019.data.db

import androidx.lifecycle.*
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntity
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionSpeakerJoinEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSpeakerEntities
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.channels.LinkedListChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.map
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class RoomSessionDatabase @Inject constructor(
    private val sessionDatabase: SessionCacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao
) : SessionDatabase {
    override fun sessionsChannel(): ReceiveChannel<List<SessionEntity>> = sessionDao.sessionsLiveData().observeChannel().map {
        it.orEmpty()
    }

    override suspend fun sessions(): List<SessionWithSpeakers> = sessionSpeakerJoinDao.getAllSessions()

    override suspend fun allSpeaker(): List<SpeakerEntity> = speakerDao.getAllSpeaker()

    override suspend fun save(apiResponse: Response) {
        withContext(Dispatchers.IO) {
            // FIXME: SQLiteDatabaseLockedException
//            sessionDatabase.runInTransaction {
            speakerDao.clearAndInsert(apiResponse.speakers.orEmpty().toSpeakerEntities())
            val sessions = apiResponse.sessions
            val sessionEntities = sessions.toSessionEntities(apiResponse.categories.orEmpty(),
                apiResponse.rooms.orEmpty())
            sessionDao.clearAndInsert(sessionEntities)
            sessionSpeakerJoinDao.insert(sessions.toSessionSpeakerJoinEntities())
//            }
        }
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
