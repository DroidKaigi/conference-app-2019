package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo

data class CategoryEntityImpl(
    @ColumnInfo(name = "category_id")
    override var id: Int,
    @ColumnInfo(name = "category_name")
    override var name: String,
    @ColumnInfo(name = "category_name_ja")
    override var jaName: String,
    @ColumnInfo(name = "category_name_en")
    override var enName: String
) : CategoryEntity
