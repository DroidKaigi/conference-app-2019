package io.github.droidkaigi.confsched2019.model

data class ContributorContents(
    val contributors: List<Contributor>
) {
    companion object {
        val empty = ContributorContents(emptyList())
    }
}
