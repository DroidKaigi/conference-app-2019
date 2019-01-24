package io.github.droidkaigi.confsched2019.model

@AndroidParcelize
data class Room(
    val id: Int,
    val name: String
) : AndroidParcel {
    fun isFirstFloor(): Boolean {
        return name.contains("Hall")
    }

    val sequentialNumber: Int
        get() {
            return when (id) {
                3869 -> 0
                3870 -> 1
                3871 -> 2
                3872 -> 3
                3873 -> 4
                3874 -> 5
                3959 -> 6
                3875 -> 7
                3876 -> 8
                else -> 9
            }
        }
}
