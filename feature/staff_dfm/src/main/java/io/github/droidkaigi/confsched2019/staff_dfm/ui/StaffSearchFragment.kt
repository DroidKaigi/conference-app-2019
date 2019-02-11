package io.github.droidkaigi.confsched2019.staff_dfm.ui

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
import androidx.fragment.app.Fragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import dagger.Component
import io.github.droidkaigi.confsched2019.App
import io.github.droidkaigi.confsched2019.di.AppComponent
import io.github.droidkaigi.confsched2019.di.PageComponent
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.di.getPageComponent
import io.github.droidkaigi.confsched2019.ext.changed
import io.github.droidkaigi.confsched2019.ext.requireValue
import io.github.droidkaigi.confsched2019.staff_dfm.R
import io.github.droidkaigi.confsched2019.staff_dfm.databinding.FragmentStaffSearchBinding
import io.github.droidkaigi.confsched2019.staff_dfm.ui.actioncreator.StaffSearchActionCreator
import io.github.droidkaigi.confsched2019.staff_dfm.ui.di.PageModule
import io.github.droidkaigi.confsched2019.staff_dfm.ui.item.StaffItem
import io.github.droidkaigi.confsched2019.staff_dfm.ui.store.StaffSearchStore
import javax.inject.Inject

class StaffSearchFragment : Fragment() {
    private lateinit var binding: FragmentStaffSearchBinding

    @Inject lateinit var searchActionCreator: StaffSearchActionCreator
    @Inject lateinit var searchStore: StaffSearchStore
    private var searchView: SearchView? = null

    private val groupAdapter = GroupAdapter<ViewHolder>()

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
        val appComponent = (requireContext().applicationContext as App).appCmponent
        getPageComponent(this) { pageLifecycleOwner ->
            DaggerStaffPageComponent.builder()
                .appComponent(appComponent)
                .pageModule(PageModule(pageLifecycleOwner))
                .build()
        }.inject(this)

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

@PageScope
@Component(
    modules = [PageModule::class],
    dependencies = [AppComponent::class]
)
interface StaffPageComponent : PageComponent {
    @Component.Builder
    interface Builder {
        fun pageModule(pageModule: PageModule): Builder
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): StaffPageComponent
    }

    fun inject(fragment: StaffSearchFragment)
}
