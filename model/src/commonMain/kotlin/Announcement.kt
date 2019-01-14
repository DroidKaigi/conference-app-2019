package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime

data class Announcement(
    val id: Long,
    val title: String,
    val content: String,
    val publishedAt: DateTime,
    val type: Type
) {
    enum class Type {
        NOTIFICATION,
        ALERT,
        FEEDBACK
    }
}
