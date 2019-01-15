package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcement")
class AnnouncementEntityImpl(
    @PrimaryKey override val id: Long,
    override val title: String,
    override val content: String,
    override val type: String,
    override val publishedAt: Long,
    override val lang: String
) : AnnouncementEntity
