package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSearchBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SearchActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SearchStore
import io.github.droidkaigi.confsched2019.session.ui.item.SpecialSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.SpeechSessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.SessionsItemDecoration
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SearchFragment : DaggerFragment() {
    private lateinit var binding: FragmentSearchBinding

    @Inject lateinit var searchActionCreator: SearchActionCreator
    @Inject lateinit var speechSessionItemFactory: SpeechSessionItem.Factory
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    private var searchView: SearchView? = null
    @Inject lateinit var searchStoreProvider: Provider<SearchStore>
    private val searchStore: SearchStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(searchStoreProvider)
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.searchRecycler.adapter = groupAdapter
        binding.searchRecycler.addItemDecoration(
            SessionsItemDecoration(requireContext(), groupAdapter)
        )

        sessionContentsStore.sessionContents.changed(viewLifecycleOwner) { contents ->
            searchActionCreator.search(
                searchView?.query?.toString(),
                sessionContentsStore.sessionContents.requireValue()
            )
        }
        searchStore.searchResult.changed(viewLifecycleOwner) { result ->
            val items = result.sessions
                .map<Session, Item<*>> { session ->
                    when (session) {
                        // TODO: Speaker search
                        is Session.SpeechSession ->
                            speechSessionItemFactory.create(session)
                        is Session.SpecialSession ->
                            SpecialSessionItem(session)
                    }
                }
            groupAdapter.update(items)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar_search, menu)
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView?.let { searchView ->
            searchView.isIconified = false
            searchView.clearFocus()
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    searchActionCreator.search(
                        query,
                        sessionContentsStore.sessionContents.requireValue()
                    )
                    return false
                }
            })
            searchView.setOnCloseListener { false }
        }
    }
}

@Module
abstract class SearchFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @PageScope
        fun providesLifecycle(searchFragment: SearchFragment): Lifecycle {
            return searchFragment.viewLifecycleOwner.lifecycle
        }

        @JvmStatic @Provides fun provideActivity(
            searchFragment: SearchFragment
        ): FragmentActivity {
            return searchFragment.requireActivity()
        }
    }
}
