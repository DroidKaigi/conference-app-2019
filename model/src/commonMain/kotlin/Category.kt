package io.github.droidkaigi.confsched2019.model

@AndroidParcelize
data class Category(
    val id: Int,
    val name: LocaledString
) : AndroidParcel
