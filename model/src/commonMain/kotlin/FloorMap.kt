package io.github.droidkaigi.confsched2019.model

sealed class FloorMap {

    abstract val name: String
    abstract val drawableResName: String

    object Floor1 : FloorMap() {
        override val name = "1F"
        override val drawableResName = "ic_floor1"
    }

    object Floor5 : FloorMap() {
        override val name = "5F"
        override val drawableResName = "ic_floor2"
    }

    companion object {
        val floorList = listOf(Floor1, Floor5)
    }
}
