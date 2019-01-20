package io.github.droidkaigi.confsched2019.model

data class StaffContents(
    val staffs: List<Staff>
) {
    fun search(query: String): StaffSearchResult =
        StaffSearchResult(
            staffs.filter {
                find(query, it.name)
            },
            query
        )

    private fun find(query: String, vararg strings: String?): Boolean =
        strings.find { it?.contains(query, ignoreCase = true) ?: false } != null

    companion object {
        val EMPTY = StaffContents(listOf())
    }
}
