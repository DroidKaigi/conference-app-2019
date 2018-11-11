package io.github.droidkaigi.confsched2019.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.ext.android.await
import io.github.droidkaigi.confsched2019.model.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject

// waiting https://github.com/Kotlin/kotlinx.coroutines/pull/523
@ExperimentalCoroutinesApi
class FirestoreImpl @Inject constructor() : FireStore {

    override suspend fun getFavoriteSessionChannel(): ReceiveChannel<List<Int>> {
        val favoritesRef = getFavoritesRef()
        val snapshot = favoritesRef
            .fastGet()
        if (snapshot.isEmpty) {
            favoritesRef.add(mapOf("initialized" to true)).await()
        }
        val channel = Channel<List<Int>>(Channel.CONFLATED)
        val listenerRegistration = favoritesRef.whereEqualTo("favorite", true)
            .addSnapshotListener(
                MetadataChanges.INCLUDE
            ) { favoriteSnapshot, _ ->
                favoriteSnapshot ?: return@addSnapshotListener
                val element = favoriteSnapshot.mapNotNull { it.id.toIntOrNull() }
                channel.offer(element)
            }
        channel.invokeOnClose {
            listenerRegistration.remove()
        }
        return channel
    }

    override suspend fun getFavoriteSessionIds(): List<Int> {
        if (FirebaseAuth.getInstance().currentUser?.uid == null) return listOf()
        val favoritesRef = getFavoritesRef()
        val snapshot = favoritesRef
            .fastGet()
        if (snapshot.isEmpty) {
            favoritesRef.add(mapOf("initialized" to true)).await()
        }

        val favorites = favoritesRef.whereEqualTo("favorite", true).fastGet()
        return favorites.documents.mapNotNull { it.id.toIntOrNull() }
    }

    override suspend fun toggleFavorite(sessionId: String) {
        val document = getFavoritesRef().document(sessionId).fastGet()
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
        val firebaseUserId = firebaseAuth.currentUser?.uid ?: throw RuntimeException(
            "RuntimeException"
        )
        return FirebaseFirestore
            .getInstance()
            .collection("users/$firebaseUserId/favorites")
    }

    override suspend fun getAnnouncements(): List<Post> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("published", true)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()
        val posts = toPosts(snapshot)
        return posts
    }
}

private fun FirestoreImpl.toPosts(
    snapshot: QuerySnapshot
): List<Post> {
    val postEntities: List<PostEntity> = snapshot
        .map { it.toObject(PostEntity::class.java) }
    val posts = postEntities.map {
        Post(it.title!!, it.content!!, DateTime(it.date!!.time))//, it.type)
    }
    return posts
}

private suspend fun DocumentReference.fastGet(): DocumentSnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}

private suspend fun Query.fastGet(): QuerySnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}

private suspend fun CollectionReference.fastGet(): QuerySnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}
