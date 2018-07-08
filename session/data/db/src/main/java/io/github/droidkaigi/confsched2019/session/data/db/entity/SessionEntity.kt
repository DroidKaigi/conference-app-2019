package io.github.droidkaigi.confsched2019.session.data.db.entity
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(tableName = "session")
data class SessionEntity(
        @PrimaryKey var id: String,
        var title: String,
        var desc: String,
        var stime: Instant,
        var etime: Instant,
        var sessionFormat: String,
        var language: String
//        @Embedded var level: LevelEntity,
//        @Embedded var topic: TopicEntity,
//        @Embedded var room: RoomEntity,
//        @Embedded val message: MessageEntity?
)