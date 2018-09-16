package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntityImpl(
        @PrimaryKey override var id: String,
        override var title: String,
        override var desc: String,
        override var stime: Long,
        override var etime: Long
//        var sessionFormat: String,
//        var language: String
//        @Embedded var level: LevelEntity,
//        @Embedded var topic: TopicEntity,
//        @Embedded var room: RoomEntity,
//        @Embedded val message: MessageEntity?
) : SessionEntity
