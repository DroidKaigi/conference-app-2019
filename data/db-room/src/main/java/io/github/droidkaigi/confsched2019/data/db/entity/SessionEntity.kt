package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntity(
        @PrimaryKey var id: String,
        var title: String,
        var desc: String,
        var stime: Long,
        var etime: Long
//        var sessionFormat: String,
//        var language: String
//        @Embedded var level: LevelEntity,
//        @Embedded var topic: TopicEntity,
//        @Embedded var room: RoomEntity,
//        @Embedded val message: MessageEntity?
)
