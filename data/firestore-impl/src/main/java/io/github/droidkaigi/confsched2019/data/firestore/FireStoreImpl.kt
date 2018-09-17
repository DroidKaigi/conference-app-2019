package io.github.droidkaigi.confsched2019.data.firestore

import javax.inject.Inject

// waiting https://github.com/Kotlin/kotlinx.coroutines/pull/523
class FireStoreImpl @Inject constructor() : FireStore {
    override fun getFavoriteSessionIds(): List<Int> {
        return listOf<Int>()
    }

    override fun toggleFavorite(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
