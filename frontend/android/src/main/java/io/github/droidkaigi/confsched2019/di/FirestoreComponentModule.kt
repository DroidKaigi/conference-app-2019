package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.firestore.Firestore
import io.github.droidkaigi.confsched2019.data.repository.FirestoreComponent
import io.github.droidkaigi.confsched2019.ext.android.Dispatchers
import javax.inject.Singleton

@Module
object FirestoreComponentModule {
    @JvmStatic @Provides @Singleton fun provideRepository(): Firestore {
        return FirestoreComponent.builder()
            .coroutineContext(Dispatchers.IO)
            .build()
            .firestore()
    }
}
