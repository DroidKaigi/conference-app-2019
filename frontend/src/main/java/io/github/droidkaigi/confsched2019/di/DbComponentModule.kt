package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.DbComponent
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import kotlinx.coroutines.experimental.CommonPool
import javax.inject.Singleton

@Module
object DbComponentModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideItemStore(application: Application): SessionDatabase {
        return DbComponent.builder()
                .context(application)
                .coroutineContext(CommonPool)
                .filename("droidkaigi.db")
                .build()
                .sessionDatabase()
    }
}
