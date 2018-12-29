package io.github.droidkaigi.confsched2019.model

data class SponsorCategory(
    val category: String,
    val index: Int,
    val sponsors: List<Sponsor>
)
