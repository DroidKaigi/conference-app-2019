package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class LanguageEntityImpl(
    @ColumnInfo(name = "language_id")
    override var id: Int,
    @ColumnInfo(name = "language_name")
    override var name: String,
    @ColumnInfo(name = "language_name_ja")
    override var jaName: String,
    @ColumnInfo(name = "language_name_en")
    override var enName: String
) : LanguageEntity
