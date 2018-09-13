package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import kotlinx.coroutines.experimental.Dispatchers
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideRepository(database: SessionDatabase): SessionRepository {
        return RepositoryComponent.builder()
                .database(database)
                .coroutineContext(Dispatchers.Default)
                .build()
                .sessionRepository()
    }
}
