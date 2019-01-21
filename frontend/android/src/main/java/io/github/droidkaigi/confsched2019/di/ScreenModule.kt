package io.github.droidkaigi.confsched2019.di

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ui.ScreenViewModel
import io.github.droidkaigi.confsched2019.util.ScreenLifecycle

@Module
object ScreenModule {

    @JvmStatic @Provides fun provideScreenViewModel(
        activity: FragmentActivity
    ): ScreenViewModel {
        return ViewModelProviders.of(activity).get(ScreenViewModel::class.java)
    }

    @JvmStatic @Provides fun provideScreenLifecycle(
        screenViewModel: ScreenViewModel
    ): ScreenLifecycle {
        return screenViewModel.lifecycle
    }
}
