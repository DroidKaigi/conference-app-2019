package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speaker")
class SpeakerEntityImpl(
    @PrimaryKey override var id: String,
    @ColumnInfo(name = "speaker_name")
    override var name: String,
    @ColumnInfo(name = "speaker_tag_line")
    override var tagLine: String?,
    @ColumnInfo(name = "speaker_bio")
    override var bio: String?,
    @ColumnInfo(name = "speaker_image_url")
    override var imageUrl: String?,
    @ColumnInfo(name = "speaker_twitter_url")
    override var twitterUrl: String?,
    @ColumnInfo(name = "speaker_company_url")
    override var companyUrl: String?,
    @ColumnInfo(name = "speaker_blog_url")
    override var blogUrl: String?,
    @ColumnInfo(name = "speaker_github_url")
    override var githubUrl: String?
) : SpeakerEntity
