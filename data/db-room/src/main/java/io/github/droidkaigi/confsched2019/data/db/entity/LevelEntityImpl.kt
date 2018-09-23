package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class LevelEntityImpl(
    @ColumnInfo(name = "level_id")
    override var id: Int,
    @ColumnInfo(name = "level_name")
    override var name: String
) : LevelEntity
