package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sponsor")
data class SponsorEntityImpl(
    @PrimaryKey
    override var name: String,
    override var url: String,
    override var image: String,
    override var category: String,
    override var categoryIndex: Int
) : SponsorEntity
