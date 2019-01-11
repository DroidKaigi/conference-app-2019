package io.github.droidkaigi.confsched2019.model

sealed class FloorMap {

    abstract val title: String

    object Floor1 : FloorMap() {
        override val title = "1F"
    }

    object Floor5 : FloorMap() {
        override val title = "5F"
    }

    companion object {
        val floorList = listOf(Floor1, Floor5)
    }
}
