package io.github.droidkaigi.confsched2019.data.repository

import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import javax.inject.Singleton
import kotlin.coroutines.experimental.CoroutineContext

@Singleton
@Component(modules = [
    RepositoryModule::class
])
interface RepositoryComponent {
    fun sessionRepository(): SessionRepository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun database(database: SessionDatabase): Builder

        @BindsInstance
        fun coroutineContext(coroutineContext: CoroutineContext): Builder

        fun build(): RepositoryComponent
    }

    companion object {
        fun builder(): Builder = DaggerRepositoryComponent.builder()
    }
}
