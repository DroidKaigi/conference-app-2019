package io.github.droidkaigi.confsched2019.model

@AndroidParcelize
data class Room(
    val id: Int,
    val name: String
) : AndroidParcel
