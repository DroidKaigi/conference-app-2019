package io.github.droidkaigi.confsched2019.session.data.db

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionEntity

interface SessionDatabase {
    fun getAllSessions(): LiveData<List<SessionEntity>>
    fun save(sessions: List<SessionEntity>)
}