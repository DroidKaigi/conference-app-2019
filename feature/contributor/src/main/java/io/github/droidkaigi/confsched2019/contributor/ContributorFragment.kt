package io.github.droidkaigi.confsched2019.contributor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.contributor.action.ContributorActionCreator
import io.github.droidkaigi.confsched2019.contributor.bindingmodel.ContributorBindingModel
import io.github.droidkaigi.confsched2019.contributor.databinding.FragmentContributorBinding
import io.github.droidkaigi.confsched2019.contributor.item.ContributorItem
import io.github.droidkaigi.confsched2019.contributor.store.ContributorStore
import io.github.droidkaigi.confsched2019.contributor.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class ContributorFragment : DaggerFragment() {
    private lateinit var binding: FragmentContributorBinding

    @Inject
    lateinit var contributorStoreProvider: Provider<ContributorStore>
    private val contributorStore: ContributorStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(contributorStoreProvider)
    }

    @Inject
    lateinit var contributorActionCreator: ContributorActionCreator
    @Inject
    lateinit var activityActionCreator: ActivityActionCreator

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()
    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContributorBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.contributorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.contributorRecyclerView.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        contributorStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it.isLoading
        }

        contributorStore.contributorList.changed(viewLifecycleOwner) { result ->
            val itemList = mutableListOf<Item<*>>()
            itemList += result.contributors
                .mapIndexed { index, item ->
                    ContributorBindingModel(index, item.name, item.iconUrl, item.profileUrl)
                }
                .map {
                    ContributorItem(it) { profileUrl ->
                        activityActionCreator.openUrl(profileUrl)
                    }
                }
            groupAdapter.update(
                itemList
            )
        }

        contributorActionCreator.load()
    }
}

@Module
abstract class ContributorFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycle(
            contributorFragment: ContributorFragment
        ): Lifecycle {
            return contributorFragment.viewLifecycleOwner.lifecycle
        }
    }
}
