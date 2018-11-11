package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime

data class Post(
    val title: String,
    val content: String,
    val date: DateTime//,
//    val type: Type
) {
//    enum class Type {
//        TUTORIAL,
//        NOTIFICATION,
//        ALERT,
//        FEEDBACK,
//    }
}
