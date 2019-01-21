package io.github.droidkaigi.confsched2019.model

@AndroidParcelize
data class Speaker(
    val id: String,
    val name: String,
    val tagLine: String?,
    val bio: String?,
    val imageUrl: String?,
    val twitterUrl: String?,
    val githubUrl: String?,
    val blogUrl: String?,
    val companyUrl: String?
) : AndroidParcel
