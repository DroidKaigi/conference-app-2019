package io.github.droidkaigi.confsched2019.data.repository

import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModule.Providers::class])
internal abstract class RepositoryModule {
    @Binds
    abstract fun sessionDatabase(impl: DataSessionRepository): SessionRepository

    @Module
    internal object Providers {
    }
}
