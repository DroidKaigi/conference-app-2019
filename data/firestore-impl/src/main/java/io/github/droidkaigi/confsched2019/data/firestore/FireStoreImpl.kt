package io.github.droidkaigi.confsched2019.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.github.droidkaigi.confsched2019.ext.android.await
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

// waiting https://github.com/Kotlin/kotlinx.coroutines/pull/523
class FireStoreImpl @Inject constructor() : FireStore {
    class FavoritesCache(val expiredInstant: Instant = Instant.now().plus(1, ChronoUnit.HOURS))

    override suspend fun getFavoriteSessionIds(): List<Int> {
        val favoritesRef = getFavoritesRef()
        val snapshot = favoritesRef
            .get().await()
        if (snapshot.isEmpty) {
            favoritesRef.add(mapOf("initialized" to true)).await()
        }
        val favorites = favoritesRef.whereEqualTo("favorite", true).get().await()
        return favorites.documents.mapNotNull { it.id.toIntOrNull() }
    }

    override suspend fun toggleFavorite(sessionId: String) {
        val document = getFavoritesRef().document(sessionId).get().await()
        val nowFavorite = document.exists() && (document.data?.get(sessionId) == true)
        val newFavorite = !nowFavorite
        if (document.exists()) {
            document.reference
                .delete()
                .await()
        } else {
            document.reference
                .set(mapOf("favorite" to newFavorite))
                .await()
        }
    }

    fun getFavoritesRef(): CollectionReference {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUserId = firebaseAuth.currentUser?.uid ?: throw RuntimeException("RuntimeException")
        return FirebaseFirestore
            .getInstance()
            .collection("users/${firebaseUserId}/favorites")
    }
}
