package io.github.droidkaigi.confsched2019.data.api.response

interface CategoryResponse {
    val id: Int?
    val sort: Int?
    val title: String?
    val items: List<CategoryItemResponse?>?
}
