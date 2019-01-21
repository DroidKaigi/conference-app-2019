package io.github.droidkaigi.confsched2019.di

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ui.MainActivity
import io.github.droidkaigi.confsched2019.ui.ScreenViewModel
import io.github.droidkaigi.confsched2019.util.ScreenLifecycle

@Module
class ScreenModule(val viewModel: ScreenViewModel) {

    @Provides fun provideScreenLifecycle(): ScreenLifecycle {
        return viewModel.lifecycle
    }
}
