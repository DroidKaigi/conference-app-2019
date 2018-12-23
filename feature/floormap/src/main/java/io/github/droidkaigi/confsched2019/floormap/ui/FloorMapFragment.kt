package io.github.droidkaigi.confsched2019.floormap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.floormap.R
import io.github.droidkaigi.confsched2019.floormap.databinding.FragmentFloorMapBinding
import io.github.droidkaigi.confsched2019.floormap.ui.actioncreator.FloorMapActionCreator
import io.github.droidkaigi.confsched2019.floormap.ui.store.FloorMapStore
import io.github.droidkaigi.confsched2019.floormap.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class FloorMapFragment : DaggerFragment() {

    lateinit var binding: FragmentFloorMapBinding

    @Inject lateinit var floorMapStoreProvider: Provider<FloorMapStore>
    private val floorMapStore: FloorMapStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[floorMapStoreProvider]
    }
    @Inject lateinit var floorMapActionCreator: FloorMapActionCreator

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_floor_map,
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
        floorMapStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
        floorMapActionCreator.load()
    }
}

@Module
abstract class FloorMapFragmentModule {
    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            floorMapFragment: FloorMapFragment
        ): Lifecycle {
            return floorMapFragment.viewLifecycleOwner.lifecycle
        }
    }
}
