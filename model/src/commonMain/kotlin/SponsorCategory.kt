package io.github.droidkaigi.confsched2019.model

data class SponsorCategory(
    val category: Category,
    val index: Int,
    val sponsors: List<Sponsor>
) {

    enum class Category(val title: String) {
        PLATINUM("platinum\nsponsors"),
        GOLD("gold\nsponsors"),
        SUPPORT("sponsors"),
        TECH("technical support\nfor network");

        companion object {
            fun from(category: String) = when(category) {
                "platinum" -> PLATINUM
                "gold" -> GOLD
                "support" -> SUPPORT
                "tech" -> TECH
                else -> null
            }
        }
    }
}
