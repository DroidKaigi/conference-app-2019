package io.github.droidkaigi.confsched2019.contributor.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_ContributorAssistedInjectModule::class])
abstract class ContributorAssistedInjectModule
