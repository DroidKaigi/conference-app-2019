package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import kotlinx.coroutines.experimental.Dispatchers
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideRepository(
        api: SessionApi,
        database: SessionDatabase,
        fireStore: FireStore
    ): SessionRepository {
        return RepositoryComponent.builder()
            .api(api)
            .database(database)
            .fireStore(fireStore)
            .coroutineContext(Dispatchers.Default)
            .build()
            .sessionRepository()
    }
}
