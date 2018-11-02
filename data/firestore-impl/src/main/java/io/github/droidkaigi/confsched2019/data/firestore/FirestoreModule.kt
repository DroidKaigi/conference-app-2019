package io.github.droidkaigi.confsched2019.data.firestore

import dagger.Binds
import dagger.Module

@Module(includes = [FirestoreModule.Providers::class])
internal abstract class FirestoreModule {
    @Binds abstract fun fireStore(impl: FirestoreImpl): FireStore

    @Module
    internal object Providers
}
