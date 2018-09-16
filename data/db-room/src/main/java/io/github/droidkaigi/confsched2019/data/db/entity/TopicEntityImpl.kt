package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class TopicEntityImpl(
        @ColumnInfo(name = "topic_id")
        override var id: Int,
        @ColumnInfo(name = "topic_name")
        override var name: String
) : TopicEntity
