package io.github.droidkaigi.confsched2019.session.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_SessionAssistedInjectModule::class])
abstract class SessionAssistedInjectModule
