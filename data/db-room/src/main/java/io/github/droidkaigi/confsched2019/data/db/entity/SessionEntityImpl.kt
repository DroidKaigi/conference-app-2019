package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntityImpl(
    @PrimaryKey override var id: String,
    override val isServiceSession: Boolean,
    override var englishTitle: String?,
    override var title: String,
    override var desc: String,
    override var stime: Long,
    override var etime: Long,
    override var sessionFormat: String?,
    @Embedded override var language: LanguageEntityImpl?,
    @Embedded override val category: CategoryEntityImpl?,
    @Embedded override val room: RoomEntityImpl?,
    override val intendedAudience: String?,
    @Embedded override val message: MessageEntityImpl?
) : SessionEntity
