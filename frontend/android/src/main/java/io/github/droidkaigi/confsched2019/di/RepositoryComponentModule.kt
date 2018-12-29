package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.api.SponsorApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @JvmStatic @Provides @Singleton fun provideRepository(
        api: SessionApi,
        database: SessionDatabase,
        sponsorApi: SponsorApi,
        sponsorDatabase: SponsorDatabase,
        fireStore: FireStore
    ): SessionRepository {
        return RepositoryComponent.builder()
            .api(api)
            .database(database)
            .sponsorApi(sponsorApi)
            .sponsorDatabase(sponsorDatabase)
            .fireStore(fireStore)
            .coroutineContext(Dispatchers.Default)
            .build()
            .sessionRepository()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorRepository(
        api: SessionApi,
        database: SessionDatabase,
        sponsorApi: SponsorApi,
        sponsorDatabase: SponsorDatabase,
        fireStore: FireStore
    ): SponsorRepository {
        return RepositoryComponent.builder()
            .api(api)
            .database(database)
            .sponsorApi(sponsorApi)
            .sponsorDatabase(sponsorDatabase)
            .fireStore(fireStore)
            .coroutineContext(Dispatchers.Default)
            .build()
            .sponsorRepository()
    }
}
