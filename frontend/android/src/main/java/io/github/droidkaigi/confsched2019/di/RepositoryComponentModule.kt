package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @JvmStatic @Provides @Singleton fun provideRepository(
        droidKaigiApi: DroidKaigiApi,
        googleFormApi: GoogleFormApi,
        database: SessionDatabase,
        sponsorDatabase: SponsorDatabase,
        fireStore: FireStore
    ): SessionRepository {
        return RepositoryComponent.builder()
            .droidKaigiApi(droidKaigiApi)
            .googleFormApi(googleFormApi)
            .database(database)
            .sponsorDatabase(sponsorDatabase)
            .fireStore(fireStore)
            .build()
            .sessionRepository()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorRepository(
        droidKaigiApi: DroidKaigiApi,
        googleFormApi: GoogleFormApi,
        database: SessionDatabase,
        sponsorDatabase: SponsorDatabase,
        fireStore: FireStore
    ): SponsorRepository {
        return RepositoryComponent.builder()
            .droidKaigiApi(droidKaigiApi)
            .googleFormApi(googleFormApi)
            .database(database)
            .sponsorDatabase(sponsorDatabase)
            .fireStore(fireStore)
            .build()
            .sponsorRepository()
    }
}
