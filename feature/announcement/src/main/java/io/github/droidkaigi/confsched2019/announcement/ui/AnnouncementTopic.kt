package io.github.droidkaigi.confsched2019.announcement.ui

import android.content.Context
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.notification.Topic
import io.github.droidkaigi.confsched2019.notification.service.SubscribeTopicIntentService

fun Context.subscribeAnnouncementTopic(lang: Lang = defaultLang()) {
    SubscribeTopicIntentService.start(
        this, when (lang) {
            Lang.EN -> Topic.EnAnnouncement
            Lang.JA -> Topic.JaAnnouncement
        }
    )
}
