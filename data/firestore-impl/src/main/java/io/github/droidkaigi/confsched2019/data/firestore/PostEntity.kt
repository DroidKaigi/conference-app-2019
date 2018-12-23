package io.github.droidkaigi.confsched2019.data.firestore

import java.util.Date

data class PostEntity(
    val title: String? = null,
    val content: String? = null,
    val date: Date? = null,
    val published: Boolean? = null,
    val type: String? = null
)
