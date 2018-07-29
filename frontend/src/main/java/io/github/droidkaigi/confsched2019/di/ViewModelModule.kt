package io.github.droidkaigi.confsched2019.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.droidkaigi.confsched2019.session.store.AllSessionsStore

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    // FIXME: move to each ActivityModule()
    // I can't fix this error :(
    // e: /Users/takahirom/git/conferenceapp2019/frontend/build/tmp/kapt3/stubs/debug/io/github/droidkaigi/confsched2019/di/AppComponent.java:8: エラー: [Dagger/MissingBinding] [dagger.android.AndroidInjector.inject(T)] java.util.Map<java.lang.Class<? extends androidx.lifecycle.ViewModel>,javax.inject.Provider<androidx.lifecycle.ViewModel>> cannot be provided without an @Provides-annotated method.
    @Binds
    @IntoMap
    @ViewModelKey(AllSessionsStore::class)
    fun bindAllSessionsStore(
            allSessionsStore: AllSessionsStore
    ): ViewModel


}