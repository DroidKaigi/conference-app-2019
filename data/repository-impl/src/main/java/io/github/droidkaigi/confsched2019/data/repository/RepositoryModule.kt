package io.github.droidkaigi.confsched2019.data.repository

import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModule.Providers::class])
internal abstract class RepositoryModule {
    @Binds abstract fun sessionDatabase(impl: DataSessionRepository): SessionRepository

    @Binds abstract fun sponsorDatabase(impl: DataSponsorRepository): SponsorRepository

    @Binds
    abstract fun announcementDatabase(impl: DataAnnouncementRepository): AnnouncementRepository

    @Binds abstract fun contributorDatabase(impl: DataContributorRepository): ContributorRepository

    @Binds abstract fun staffDatabase(impl: DataStaffRepository): StaffRepository

    @Binds
    abstract fun wifiConfigurationRepository(impl: DataWifiConfigurationRepository): WifiConfigurationRepository

    @Module
    internal object Providers
}
