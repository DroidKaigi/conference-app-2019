package io.github.droidkaigi.confsched2019.sponsor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.item.DividerItem
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.FragmentSponsorBinding
import io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator.SponsorActionCreator
import io.github.droidkaigi.confsched2019.sponsor.ui.item.HeaderItem
import io.github.droidkaigi.confsched2019.sponsor.ui.item.SponsorItem
import io.github.droidkaigi.confsched2019.sponsor.ui.item.TallSponsorItem
import io.github.droidkaigi.confsched2019.sponsor.ui.store.SponsorStore
import io.github.droidkaigi.confsched2019.sponsor.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
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
    @Inject lateinit var activityActionCreator: ActivityActionCreator

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

        binding.sponsorRecycler.layoutManager = GridLayoutManager(
            requireContext(),
            groupAdapter.spanCount
        ).apply {
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
        sponsorStore.sponsors.changed(viewLifecycleOwner, this::setupSponsorsLayout)

        sponsorActionCreator.load()
    }

    private fun setupSponsorsLayout(sponsorCategories: List<SponsorCategory>) {
        val sponsors = sponsorCategories.map { category ->
            category.toSection(sponsorCategories.last() == category)
        }
        groupAdapter.update(sponsors)
    }

    private fun SponsorCategory.toSection(isLastItem: Boolean) = Section().apply {
        setHeader(HeaderItem(category.title))
        addAll(
            sponsors.map { sponsor ->
                sponsor.toItem(category)
            }
        )
        if (!isLastItem) {
            setFooter(DividerItem())
        }
        setHideWhenEmpty(true)
    }

    private fun Sponsor.toItem(category: SponsorCategory.Category): Item<*> {
        val spanSize = when (category) {
            SponsorCategory.Category.PLATINUM -> 2
            else -> 1
        }
        return when (category) {
            SponsorCategory.Category.PLATINUM,
            SponsorCategory.Category.GOLD -> {
                TallSponsorItem(this, spanSize) { sponsorUrl ->
                    activityActionCreator.openUrl(sponsorUrl)
                }
            }
            else -> {
                SponsorItem(this, spanSize) { sponsorUrl ->
                    activityActionCreator.openUrl(sponsorUrl)
                }
            }
        }
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
