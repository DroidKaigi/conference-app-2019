package io.github.droidkaigi.confsched2019.session.model

import android.content.Context
import io.github.droidkaigi.confsched2019.session.data.api.getSessions
import io.github.droidkaigi.confsched2019.session.data.api.response.Session
import io.github.droidkaigi.confsched2019.session.data.db.SessionRoomDatabase
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionEntity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class SessionActionCreator {
    suspend fun load(context: Context) = launch(CommonPool) {
        try {
            val sessions = getSessions()
            val sessionRoomDatabase = SessionRoomDatabase(context)
            sessionRoomDatabase.save(sessions.sessions.map { sessionEntity ->
                sessionEntity.toSessionEntity()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Session.toSessionEntity(): SessionEntity {
        return SessionEntity(
                id = id,
                title = title,
                desc = description,
                stime = startsAt,
                etime = endsAt,
                sessionFormat = "TODO",
                language = "TODO"
//                message = if (message != null) {
//                    MessageEntity(message.ja!!, message.en!!)
//                } else {
//                    null
//                },
//                topic = TopicEntity(topic.id!!, topic.name!!),
//                level = LevelEntity(level.id!!, level.name!!),
//                room = RoomEntity(roomId!!, rooms.roomName(roomId))
        )
    }

}

