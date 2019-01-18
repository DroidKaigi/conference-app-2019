package io.github.droidkaigi.confsched2019.session.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSearchBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SearchActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.ServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.item.SpeechSessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.SearchStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SearchFragment : DaggerFragment() {
    private lateinit var binding: FragmentSearchBinding

    @Inject lateinit var searchActionCreator: SearchActionCreator
    @Inject lateinit var speechSessionItemFactory: SpeechSessionItem.Factory
    @Inject lateinit var serviceSessionItemFactory: ServiceSessionItem.Factory
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    private var searchView: SearchView? = null
    @Inject lateinit var searchStoreProvider: Provider<SearchStore>
    private val searchStore: SearchStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(searchStoreProvider)
    }
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory

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
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.searchRecycler.adapter = groupAdapter
        sessionContentsStore.sessionContents.changed(viewLifecycleOwner) { contents ->
            searchActionCreator.search(
                searchStore.query,
                contents
            )
        }
        // TODO apply design
        searchStore.searchResult.changed(viewLifecycleOwner) { result ->
            val items = mutableListOf<Item<*>>()
            items += result.speakers.map {
                speakerItemFactory.create(
                    it,
                    SearchFragmentDirections.actionSearchToSpeaker(it.id)
                )
            }.sortedBy { it.speaker.name.toUpperCase() }

            items += result.sessions
                .map<Session, Item<*>> { session ->
                    when (session) {
                        is Session.SpeechSession ->
                            speechSessionItemFactory.create(
                                session,
                                SearchFragmentDirections.actionSearchToSessionDetail(
                                    session.id
                                ),
                                false
                            )
                        is Session.ServiceSession ->
                            serviceSessionItemFactory.create(
                                session,
                                SearchFragmentDirections.actionSearchToSessionDetail(
                                    session.id
                                )
                            )
                    }
                }
            groupAdapter.update(items)
        }
        context?.let {
            binding.searchRecycler.addItemDecoration(
                StickyHeaderItemDecoration(it,
                    getGroupId = { position ->
                        val item = groupAdapter.getItem(position)
                        when (item) {
                            is SpeakerItem -> item.speaker.name[0].toUpperCase().toLong()
                            is SpeechSessionItem ->
                                item.speechSession.title.getByLang(defaultLang())[0]
                                    .toUpperCase().toLong()
                            is ServiceSessionItem ->
                                item.serviceSession.title.getByLang(defaultLang())[0]
                                    .toUpperCase().toLong()
                            else -> StickyHeaderItemDecoration.EMPTY_ID
                        }
                    },
                    getInitial = { position ->
                        val item = groupAdapter.getItem(position)
                        when (item) {
                            is SpeakerItem -> item.speaker.name[0].toUpperCase().toString()
                            is SpeechSessionItem ->
                                item.speechSession.title.getByLang(defaultLang())[0]
                                    .toUpperCase().toString()
                            is ServiceSessionItem ->
                                item.serviceSession.title.getByLang(defaultLang())[0]
                                    .toUpperCase().toString()
                            else -> StickyHeaderItemDecoration.DEFAULT_INITIAL
                        }
                    })
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar_search, menu)
        menu.findItem(R.id.search)?.isVisible = false
        searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
        searchView?.let { searchView ->
            searchView.setQuery(searchStore.query, false)
            searchView.isIconified = false
            searchView.clearFocus()
            searchView.queryHint = getString(R.string.session_search_hint)
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
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnCloseListener { false }
        }
    }

    override fun onPause() {
        super.onPause()

        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        val view = activity?.currentFocus
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}

class StickyHeaderItemDecoration(
    context: Context,
    private val getGroupId: (Int) -> Long,
    private val getInitial: (Int) -> String
) : RecyclerView.ItemDecoration() {

    private val textPaint = TextPaint()
    private val labelPadding: Int
    private val contentMargin: Int
    private val fontMetrics: Paint.FontMetrics

    init {
        val resource = context.resources

        textPaint.apply {
            typeface = Typeface.DEFAULT
            isAntiAlias = true
            textSize = resource.getDimension(R.dimen.sticky_label_font_size)
            textAlign = Paint.Align.LEFT
        }

        fontMetrics = textPaint.fontMetrics

        labelPadding = resource.getDimensionPixelSize(R.dimen.sticky_label_padding)
        contentMargin = resource.getDimensionPixelSize(R.dimen.sticky_label_content_margin)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (view.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            outRect.right = contentMargin
        } else {
            outRect.left = contentMargin
        }
    }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)

        val totalItemCount = state.itemCount
        val childCount = parent.childCount
        val lineHeight = textPaint.textSize + fontMetrics.descent
        var previousGroupId: Long
        var groupId: Long = EMPTY_ID

        (0 until childCount).forEach {
            val view = parent.getChildAt(it)
            val position = parent.getChildAdapterPosition(view)
            if (position < 0) return@forEach
            // Acquires the first character of the immediately preceding character and the Id of the character to be checked this time
            previousGroupId = groupId
            groupId = getGroupId(position)

            // If the current element is EMPTY or the same as the previous element,
            // there is nothing (if it differs from the previous element, proceed next)
            if (groupId == EMPTY_ID || previousGroupId == groupId) return@forEach

            // Get Initial and check if it is empty character
            val initial = getInitial(position)
            if (TextUtils.isEmpty(initial)) return@forEach

            // drawing
            val viewTop = view.top + labelPadding
            val viewBottom = view.bottom + view.paddingBottom
            var textY = Math.max(labelPadding, viewTop) + lineHeight
            if (position + 1 < totalItemCount) {
                val nextGroupId = getGroupId(position + 1)
                if (nextGroupId != groupId && viewBottom < textY + lineHeight) {
                    textY = viewBottom - lineHeight
                }
            }
            c.drawText(initial, labelPadding.toFloat(), textY, textPaint)
        }
    }

    companion object {
        const val EMPTY_ID: Long = -1
        const val DEFAULT_INITIAL = "*"
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
