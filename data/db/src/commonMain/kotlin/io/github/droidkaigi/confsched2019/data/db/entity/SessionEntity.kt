package io.github.droidkaigi.confsched2019.data.db.entity

interface SessionEntity {
    var id: String
    var title: String
    var desc: String
    var stime: Long
    var etime: Long
    var sessionFormat: String
    var language: String
    val level: LevelEntity
    val topic: TopicEntity
    val room: RoomEntity
    val message: MessageEntity?
}
