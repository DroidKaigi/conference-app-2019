package io.github.droidkaigi.confsched2019.data.firestore

import dagger.Binds
import dagger.Module

@Module(includes = [FireStoreModule.Providers::class])
internal abstract class FireStoreModule {
    @Binds
    abstract fun fireStore(impl: FireStoreImpl): FireStore

    @Module
    internal object Providers
}
