package io.github.droidkaigi.confsched2019.data.repository

import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.api.SponsorApi
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
@Component(modules = [
    RepositoryModule::class
])
interface RepositoryComponent {
    fun sessionRepository(): SessionRepository
    fun sponsorRepository(): SponsorRepository

    @Component.Builder
    interface Builder {
        @BindsInstance fun api(api: SessionApi): Builder
        @BindsInstance fun sponsorApi(api: SponsorApi): Builder

        @BindsInstance fun database(database: SessionDatabase): Builder
        @BindsInstance fun sponsorDatabase(database: SponsorDatabase): Builder

        @BindsInstance fun fireStore(fireStore: FireStore): Builder

        @BindsInstance fun coroutineContext(coroutineContext: CoroutineContext): Builder

        fun build(): RepositoryComponent
    }

    companion object {
        fun builder(): Builder = DaggerRepositoryComponent.builder()
    }
}
