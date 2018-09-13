package io.github.droidkaigi.confsched2019.di

import android.app.Application
import androidx.annotation.StringRes
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.R
import javax.inject.Named

@Module
object FirebaseComponentModule {
    @JvmStatic
    @Provides
    @StringRes
    @Named("defaultFirebaseWebClientId")
    fun provideDefault(application: Application): Int {
        return R.string.default_web_client_id
    }
}
