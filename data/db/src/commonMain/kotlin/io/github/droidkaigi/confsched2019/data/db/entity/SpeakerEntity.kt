package io.github.droidkaigi.confsched2019.data.db.entity

interface SpeakerEntity {
    var id: String
    var name: String
    var tagLine: String?
    var bio: String?
    var imageUrl: String?
    var twitterUrl: String?
    var companyUrl: String?
    var blogUrl: String?
    var githubUrl: String?
}
