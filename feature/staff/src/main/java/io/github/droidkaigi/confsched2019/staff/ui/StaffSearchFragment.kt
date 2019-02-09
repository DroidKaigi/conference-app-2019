package io.github.droidkaigi.confsched2019.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.changed
import io.github.droidkaigi.confsched2019.ext.requireValue
import io.github.droidkaigi.confsched2019.staff.R
import io.github.droidkaigi.confsched2019.staff.databinding.FragmentStaffSearchBinding
import io.github.droidkaigi.confsched2019.staff.ui.actioncreator.StaffSearchActionCreator
import io.github.droidkaigi.confsched2019.staff.ui.item.StaffItem
import io.github.droidkaigi.confsched2019.staff.ui.store.StaffSearchStore
import io.github.droidkaigi.confsched2019.staff.ui.widget.DaggerFragment
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class StaffSearchFragment : DaggerFragment() {
    private lateinit var binding: FragmentStaffSearchBinding

    @Inject lateinit var searchActionCreator: StaffSearchActionCreator
    private var searchView: SearchView? = null
    @Inject lateinit var searchStoreProvider: Provider<StaffSearchStore>
    private val searchStore: StaffSearchStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(searchStoreProvider)
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_staff_search,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.searchRecycler.adapter = groupAdapter

        searchStore.loadingState.changed(viewLifecycleOwner) { loadingState ->
            binding.progressBar.isVisible = loadingState.isLoading
        }

        searchStore.staffContents.changed(viewLifecycleOwner) { staffContents ->
            searchActionCreator.search(
                searchStore.query,
                staffContents
            )
        }

        searchStore.searchResult.changed(viewLifecycleOwner) { result ->
            val items = mutableListOf<Item<*>>()
            items += result.staffs.map { staff -> StaffItem(staff) }
            groupAdapter.update(items)
        }

        if (!searchStore.loadingState.requireValue().isLoaded) {
            searchActionCreator.load()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar_staff_search, menu)

        val searchMenuItem: MenuItem = menu.findItem(R.id.menu_search)
        searchView = (searchMenuItem.actionView as SearchView).apply {
            setQuery(searchStore.query, false)
            isIconified = false
            clearFocus()
            imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
            queryHint = getString(R.string.staff_search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    searchActionCreator.search(
                        query,
                        searchStore.staffContents.requireValue()
                    )
                    return false
                }
            })
            maxWidth = Int.MAX_VALUE
            setOnCloseListener { false }
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        val view = activity?.currentFocus
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}

@Module
abstract class StaffSearchFragmentModule {
    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            staffSearchFragment: StaffSearchFragment
        ): Lifecycle {
            return staffSearchFragment.viewLifecycleOwner.lifecycle
        }
    }
}
