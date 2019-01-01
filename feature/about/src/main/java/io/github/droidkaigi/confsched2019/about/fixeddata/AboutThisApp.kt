package io.github.droidkaigi.confsched2019.about.fixeddata

sealed class AboutThisApp(
    open val id: Int,
    open val name: Int,
    open val description: Int,
    open val navigationUrl: String?
) {
    data class Item(
        override val id: Int,
        override val name: Int,
        override val description: Int,
        override val navigationUrl: String?
    ) : AboutThisApp(id, name, description, navigationUrl)

    // TODO: Determine headers link
    data class HeadItem(
        override val id: Int,
        override val name: Int,
        override val description: Int,
        override val navigationUrl: String?
    ) : AboutThisApp(id, name, description, navigationUrl)
}
