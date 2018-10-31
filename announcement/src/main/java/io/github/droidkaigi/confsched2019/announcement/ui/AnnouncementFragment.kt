package io.github.droidkaigi.confsched2019.announcement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.announcement.R
import io.github.droidkaigi.confsched2019.announcement.databinding.FragmentAnnouncementBinding
import io.github.droidkaigi.confsched2019.announcement.ui.actioncreator.AnnouncementActionCreator
import io.github.droidkaigi.confsched2019.announcement.ui.store.AnnouncementStore
import io.github.droidkaigi.confsched2019.announcement.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class AnnouncementFragment : DaggerFragment() {

    lateinit var binding: FragmentAnnouncementBinding

    @Inject lateinit var announcementStoreProvider: Provider<AnnouncementStore>
    private val announcementStore: AnnouncementStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[announcementStoreProvider]
    }
    @Inject lateinit var announcementActionCreator: AnnouncementActionCreator

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_announcement,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        announcementStore.loadingState.changed(this) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
        announcementActionCreator.load()
    }
}

@Module
abstract class AnnouncementFragmentModule {
    @Module
    companion object {
        @Named("AnnouncementFragment") @JvmStatic @Provides fun providesLifecycle(
            announcementFragment: AnnouncementFragment
        ): Lifecycle {
            return announcementFragment.viewLifecycleOwner.lifecycle
        }
    }
}
