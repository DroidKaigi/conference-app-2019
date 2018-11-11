package io.github.droidkaigi.confsched2019.data.firestore

import java.util.Date

data class PostEntity(
    var title: String?,
    var content: String?,
    var date: Date?,
    var published: Boolean?,
    var type: String?
) {
    constructor() : this(null, null, null, null, null)
}
