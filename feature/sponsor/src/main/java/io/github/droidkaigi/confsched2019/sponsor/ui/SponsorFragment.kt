package io.github.droidkaigi.confsched2019.sponsor.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.GoalRow
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.FragmentSponsorBinding
import io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator.SponsorActionCreator
import io.github.droidkaigi.confsched2019.sponsor.ui.item.HeaderItem
import io.github.droidkaigi.confsched2019.sponsor.ui.item.SponsorItem
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

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

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
        groupAdapter.spanCount = 2

        binding.sponsorRecycler.layoutManager = GridLayoutManager(requireContext(), groupAdapter.spanCount).apply {
            spanSizeLookup = groupAdapter.spanSizeLookup
        }
        binding.sponsorRecycler.adapter = groupAdapter
        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        sponsorStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
        sponsorStore.sponsors.changed(viewLifecycleOwner) { sponsorCategories ->
            sponsorCategories.map {
                Section().apply {
                    setHeader(HeaderItem(it.category.title))
                    addAll(
                        it.sponsors.map { sponsor ->
                            val spanSize = when (it.category) {
                                SponsorCategory.Category.PLATINUM -> 2
                                else -> 1
                            }
                            when (it.category) {
                                SponsorCategory.Category.PLATINUM,
                                SponsorCategory.Category.GOLD -> {
                                    SponsorItem.create(sponsor, spanSize) { sponsorUrl ->
                                        sponsorActionCreator.openSponsorLink(sponsorUrl)
                                    }
                                }
                                else -> {
                                    SponsorItem.createShort(sponsor, spanSize) { sponsorUrl ->
                                        sponsorActionCreator.openSponsorLink(sponsorUrl)
                                    }
                                }
                            }

                        }
                    )
                    setHideWhenEmpty(true)
                }
            }
                .forEach(groupAdapter::add)
        }

        sponsorStore.clickedSponsorUrl.changed(viewLifecycleOwner) {
            sponsorActionCreator.clearSponsorLink()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
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
