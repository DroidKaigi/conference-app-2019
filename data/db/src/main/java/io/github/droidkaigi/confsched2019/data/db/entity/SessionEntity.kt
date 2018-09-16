package io.github.droidkaigi.confsched2019.data.db.entity

interface SessionEntity {
    var id: String
    var title: String
    var desc: String
    var stime: Long
    var etime: Long
}
