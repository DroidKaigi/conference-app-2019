package io.github.droidkaigi.confsched2019.sponsor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.FragmentSponsorBinding
import io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator.SponsorActionCreator
import io.github.droidkaigi.confsched2019.sponsor.ui.store.SponsorStore
import io.github.droidkaigi.confsched2019.sponsor.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SponsorFragment : DaggerFragment() {

    private lateinit var binding: FragmentSponsorBinding

    @Inject lateinit var sponsorStoreProvider: Provider<SponsorStore>
    private val sponsorStore: SponsorStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[sponsorStoreProvider]
    }
    @Inject lateinit var sponsorActionCreator: SponsorActionCreator

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sponsor,
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
        sponsorStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
        sponsorStore.sponsors.changed(viewLifecycleOwner) {
            // TODO: Implement sponsor list
            println(it)
        }
        sponsorActionCreator.load()
    }
}

@Module
abstract class SponsorFragmentModule {
    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            sponsorFragment: SponsorFragment
        ): Lifecycle {
            return sponsorFragment.viewLifecycleOwner.lifecycle
        }
    }
}
