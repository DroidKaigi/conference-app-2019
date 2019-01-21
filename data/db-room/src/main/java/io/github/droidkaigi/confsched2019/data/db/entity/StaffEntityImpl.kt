package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
class StaffEntityImpl(
    @PrimaryKey override var id: String,
    @ColumnInfo(name = "staff_name")
    override var name: String,
    @ColumnInfo(name = "staff_icon_url")
    override var iconUrl: String?,
    @ColumnInfo(name = "staff_profile_url")
    override var profileUrl: String?
) : StaffEntity
