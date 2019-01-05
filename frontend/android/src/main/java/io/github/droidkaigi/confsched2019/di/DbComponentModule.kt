package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.DbComponent
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.ext.android.Dispatchers
import javax.inject.Singleton

@Module
object DbComponentModule {
    @JvmStatic @Provides @Singleton fun provideItemStore(
        application: Application
    ): SessionDatabase {
        return DbComponent.builder()
            .context(application)
            .coroutineContext(Dispatchers.IO)
            .filename("droidkaigi.db")
            .build()
            .sessionDatabase()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorStore(
        application: Application
    ): SponsorDatabase {
        return DbComponent.builder()
            .context(application)
            .coroutineContext(Dispatchers.IO)
            .filename("droidkaigi.db")
            .build()
            .sponsorDatabase()
    }
}
