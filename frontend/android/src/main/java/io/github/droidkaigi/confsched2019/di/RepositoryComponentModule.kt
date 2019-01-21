package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2019.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.db.StaffDatabase
import io.github.droidkaigi.confsched2019.data.firestore.Firestore
import io.github.droidkaigi.confsched2019.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2019.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2019.data.repository.StaffRepository
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @JvmStatic @Provides @Singleton fun provideRepository(
        repositoryComponent: RepositoryComponent
    ): SessionRepository {
        return repositoryComponent.sessionRepository()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorRepository(
        repositoryComponent: RepositoryComponent
    ): SponsorRepository {
        return repositoryComponent.sponsorRepository()
    }

    @JvmStatic @Provides @Singleton fun provideAnnouncementRepository(
        repositoryComponent: RepositoryComponent
    ): AnnouncementRepository {
        return repositoryComponent.announcementRepository()
    }

    @JvmStatic @Provides @Singleton fun provideStaffRepository(
        repositoryComponent: RepositoryComponent
    ): StaffRepository {
        return repositoryComponent.staffRepository()
    }

    @JvmStatic @Provides @Singleton fun provideRepositoryComponent(
        droidKaigiApi: DroidKaigiApi,
        googleFormApi: GoogleFormApi,
        database: SessionDatabase,
        sponsorDatabase: SponsorDatabase,
        announcementDatabase: AnnouncementDatabase,
        staffDatabase: StaffDatabase,
        firestore: Firestore
    ): RepositoryComponent {
        return RepositoryComponent.builder()
            .droidKaigiApi(droidKaigiApi)
            .googleFormApi(googleFormApi)
            .database(database)
            .sponsorDatabase(sponsorDatabase)
            .firestore(firestore)
            .announcementDatabase(announcementDatabase)
            .staffDatabase(staffDatabase)
            .build()
    }
}
