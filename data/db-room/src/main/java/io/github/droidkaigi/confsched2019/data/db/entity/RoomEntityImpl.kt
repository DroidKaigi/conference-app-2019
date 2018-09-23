package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class RoomEntityImpl(
    @ColumnInfo(name = "room_id")
    override var id: Int,
    @ColumnInfo(name = "room_name")
    override var name: String
) : RoomEntity
