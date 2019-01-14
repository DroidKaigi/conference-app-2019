package io.github.droidkaigi.confsched2019.data.firestore

import dagger.Binds
import dagger.Module

@Module(includes = [FirestoreModule.Providers::class])
internal abstract class FirestoreModule {
    @Binds abstract fun firestore(impl: FirestoreImpl): Firestore

    @Module
    internal object Providers
}
