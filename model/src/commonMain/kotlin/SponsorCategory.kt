package io.github.droidkaigi.confsched2019.model

data class SponsorCategory(
    val category: Category,
    val index: Int,
    val sponsors: List<Sponsor>
) {

    enum class Category(val id: String, val title: String) {
        PLATINUM("platinum", "platinum\nsponsors"),
        GOLD("gold", "gold\nsponsors"),
        SUPPORT("support", "sponsors"),
        TECH("tech", "technical support\nfor network");

        companion object {
            fun from(category: String) = values().firstOrNull {
                it.id == category
            }
        }
    }
}
