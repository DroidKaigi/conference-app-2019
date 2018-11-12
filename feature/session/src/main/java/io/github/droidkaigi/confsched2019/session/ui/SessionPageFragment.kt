package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.shopify.livedataktx.observe
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.model.Topic
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionFilterBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPagesActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionPageFragment : DaggerFragment() {
    lateinit var binding: FragmentSessionFilterBinding

    @Inject lateinit var sessionsActionCreator: SessionsActionCreator
    @Inject lateinit var sessionPagesActionCreator: SessionPagesActionCreator
    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    @Inject lateinit var systemStore: SystemStore
    private val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[sessionPagesStoreProvider]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_session_filter, container,
            false
        )
        return binding.root
    }

    private val args: SessionPageFragmentArgs by lazy {
        SessionPageFragmentArgs.fromBundle(arguments)
    }

    private val bottomSheetBehavior: BottomSheetBehavior<*>
        get() = BottomSheetBehavior.from(binding.sessionSheet)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment: Fragment = when (val tab = SessionTab.tabs[args.tabIndex]) {
                is SessionTab.Day -> {
                    BottomSheetDaySessionsFragment.newInstance(
                        BottomSheetDaySessionsFragmentArgs
                            .Builder(tab.day)
                            .build()
                    )
                }
                SessionTab.Favorite -> {
                    BottomSheetFavoriteSessionsFragment.newInstance()
                }
            }

            childFragmentManager
                .beginTransaction()
                .replace(R.id.session_sheet, fragment)
                .commit()
        }
        bottomSheetBehavior.isHideable = false
        binding.sessionSheet.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    binding.sessionSheet.viewTreeObserver.removeOnPreDrawListener(this)
                    if (isDetached) return false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                    return false
                }
            }
        )
        binding.filterReset.setOnClickListener {
            sessionPagesActionCreator.clearFilters()
        }

        sessionPagesStore.filtersChange.observe(viewLifecycleOwner) {
            applyFilters()
        }
        sessionPagesStore.rooms.changed(viewLifecycleOwner) { rooms ->
            binding.filterRoomChip.setupFilter(
                rooms,
                Room::name,
                sessionPagesActionCreator::changeFilter
            )
        }
        sessionPagesStore.topics.changed(viewLifecycleOwner) { topics ->
            binding.filterTopicChip.setupFilter(
                topics,
                { topics -> topics.getNameByLang(systemStore.lang) },
                sessionPagesActionCreator::changeFilter
            )
        }
        sessionPagesStore.langs.changed(viewLifecycleOwner) { langs ->
            binding.filterLangChip.setupFilter(
                langs,
                Lang::toString,
                sessionPagesActionCreator::changeFilter
            )
        }
        sessionPagesStore.selectedTab.changed(viewLifecycleOwner) {
            if (SessionTab.tabs[args.tabIndex] == it) {
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun <T> ChipGroup.setupFilter(
        items: List<T>,
        chipText: (T) -> String,
        onChecked: (T, Boolean) -> Unit
    ) {
        removeAllViews()
        items
            .map { item ->
                val chip = LayoutInflater.from(context).inflate(
                    R.layout.layout_chip,
                    this,
                    false
                ) as Chip
                chip.apply {
                    text = chipText(item)
                    tag = item
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        onChecked(item, isChecked)
                    }
                }
            }
            .forEach {
                addView(it)
            }
        applyFilters()
    }

    private fun applyFilters() {
        val filterRooms = sessionPagesStore.filters.rooms
        binding.filterRoomChip.forEach {
            if (filterRooms.isNotEmpty()) {
                val chip = it as? Chip ?: return@forEach
                val room = it.tag as? Room ?: return@forEach
                chip.isChecked = filterRooms.contains(room)
            } else {
                val chip = it as? Chip ?: return@forEach
                chip.isChecked = false
            }
        }
        val filterLangs = sessionPagesStore.filters.langs
        binding.filterLangChip.forEach {
            if (filterLangs.isNotEmpty()) {
                val chip = it as? Chip ?: return@forEach
                val lang = it.tag as? Lang ?: return@forEach
                chip.isChecked = filterLangs.contains(lang)
            } else {
                val chip = it as? Chip ?: return@forEach
                chip.isChecked = false
            }
        }
        val filterTopics = sessionPagesStore.filters.topics
        binding.filterTopicChip.forEach {
            if (filterTopics.isNotEmpty()) {
                val chip = it as? Chip ?: return@forEach
                val topic = it.tag as? Topic ?: return@forEach
                chip.isChecked = filterTopics.contains(topic)
            } else {
                val chip = it as? Chip ?: return@forEach
                chip.isChecked = false
            }
        }
    }

    companion object {
        fun newInstance(args: SessionPageFragmentArgs): SessionPageFragment {
            return SessionPageFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
object SessionFilterFragmentModule {
    @JvmStatic @Provides fun providesLifecycle(
        sessionsFragmentBottomSheet: BottomSheetDaySessionsFragment
    ): LifecycleOwner {
        return sessionsFragmentBottomSheet.viewLifecycleOwner
    }
}
