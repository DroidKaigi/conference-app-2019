package io.github.droidkaigi.confsched2019.data.db.entity

interface SessionWithSpeakers {
    var session: SessionEntity?
    var speakerIdList: List<String>
}
