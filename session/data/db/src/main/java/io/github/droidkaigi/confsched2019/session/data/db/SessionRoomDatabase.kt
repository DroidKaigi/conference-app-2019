package io.github.droidkaigi.confsched2019.session.data.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionDao
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionEntity

class SessionRoomDatabase constructor(
        context:Context,
        private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "droidkaigi.db")
                .fallbackToDestructiveMigration()
                .build(),
        private val sessionDao: SessionDao = database.sessionDao()
//        private val speakerDao: SpeakerDao,
//        private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
//        private val sessionFeedbackDao: SessionFeedbackDao
) : SessionDatabase {
    override fun getAllSessions(): LiveData<List<SessionEntity>> =
            sessionDao.getAllSessions()

    override fun save(sessions: List<SessionEntity>) {
        database.runInTransaction {
            sessionDao.clearAndInsert(sessions)
        }
    }
}