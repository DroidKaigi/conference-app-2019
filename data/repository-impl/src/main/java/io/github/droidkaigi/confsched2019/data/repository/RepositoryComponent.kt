package io.github.droidkaigi.confsched2019.data.repository

import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2019.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2019.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.db.StaffDatabase
import io.github.droidkaigi.confsched2019.data.firestore.Firestore
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class
    ]
)
interface RepositoryComponent {
    fun sessionRepository(): SessionRepository
    fun sponsorRepository(): SponsorRepository
    fun announcementRepository(): AnnouncementRepository
    fun staffRepository(): StaffRepository
    fun contributorRepository(): ContributorRepository

    @Component.Builder
    interface Builder {
        @BindsInstance fun droidKaigiApi(api: DroidKaigiApi): Builder

        @BindsInstance fun googleFormApi(api: GoogleFormApi): Builder

        @BindsInstance fun database(database: SessionDatabase): Builder
        @BindsInstance fun sponsorDatabase(database: SponsorDatabase): Builder
        @BindsInstance fun announcementDatabase(database: AnnouncementDatabase): Builder
        @BindsInstance fun staffDatabase(database: StaffDatabase): Builder
        @BindsInstance fun contributorDatabase(database: ContributorDatabase): Builder

        @BindsInstance fun firestore(firestore: Firestore): Builder

        fun build(): RepositoryComponent
    }

    companion object {
        fun builder(): Builder = DaggerRepositoryComponent.builder()
    }
}
