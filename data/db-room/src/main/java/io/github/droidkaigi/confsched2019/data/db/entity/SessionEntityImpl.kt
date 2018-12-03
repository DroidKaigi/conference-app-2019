package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntityImpl(
    @PrimaryKey override var id: String,
    override var title: String,
    override var desc: String,
    override var stime: Long,
    override var etime: Long,
    override var sessionFormat: String,
    override var language: String,
    @Embedded override val topic: TopicEntityImpl,
    @Embedded override val room: RoomEntityImpl,
    @Embedded override val message: MessageEntityImpl?
) : SessionEntity
