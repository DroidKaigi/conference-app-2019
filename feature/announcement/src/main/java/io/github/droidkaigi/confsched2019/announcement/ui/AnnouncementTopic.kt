package io.github.droidkaigi.confsched2019.announcement.ui

import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.notification.Topic
import io.github.droidkaigi.confsched2019.notification.worker.ManageTopicSubscriptionWorker

fun subscribeAnnouncementTopic(lang: Lang = defaultLang()) {
    val allAnnouncementTopics = arrayOf(Topic.JaAnnouncement, Topic.EnAnnouncement)

    val subscribeTopic = when (lang) {
        Lang.EN -> Topic.EnAnnouncement
        Lang.JA -> Topic.JaAnnouncement
    }

    ManageTopicSubscriptionWorker.start(
        subscribes = listOf(subscribeTopic),
        unsubscribes = allAnnouncementTopics.filter { it != subscribeTopic }
    )
}
