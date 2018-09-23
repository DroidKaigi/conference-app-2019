package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class MessageEntityImpl(
    @ColumnInfo(name = "message_ja")
    override var ja: String,
    @ColumnInfo(name = "message_en")
    override var en: String
) : MessageEntity
