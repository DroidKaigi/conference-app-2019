package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjection
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory
    @Inject lateinit var activityActionCreator: ActivityActionCreator

    private lateinit var progressTimeLatch: ProgressTimeLatch

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs
    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments)

        binding.sessionSpeakers.adapter = groupAdapter

        binding.bottomAppBar.replaceMenu(R.menu.menu_session_detail_bottomappbar)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.session_share -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener false
                    activityActionCreator.shareUrl(getString(
                        R.string.session_detail_share_url,
                        session.id
                    ))
                }
                R.id.session_place ->
                    Toast.makeText(
                        requireContext(),
                        "not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
            }
            return@setOnMenuItemClickListener true
        }

        sessionContentsStore.speechSession(sessionDetailFragmentArgs.session)
            .changed(viewLifecycleOwner) { session ->
                applySessionLayout(session)
            }

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }
        sessionContentsStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }

        binding.sessionFavorite.setOnClickListener {
            val session = binding.session ?: return@setOnClickListener
            progressTimeLatch.loading = true
            sessionContentsActionCreator.toggleFavorite(session)
        }
    }

    private fun applySessionLayout(session: Session.SpeechSession) {
        binding.session = session
        binding.lang = defaultLang()
        binding.timeZoneOffsetHours = 9 // FIXME Get from device setting
        @Suppress("StringFormatMatches") // FIXME
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            session.timeInMinutes,
            session.room.name
        )
        binding.sessionIntendedAudienceDescription.text = session.intendedAudience
        binding.categoryChip.text = session.category.name.getByLang(systemStore.lang)

        session.message?.let { message ->
            binding.sessionMessage.text = message.getByLang(systemStore.lang)
        }

        val sessionItems = session
            .speakers
            .map {
                speakerItemFactory.create(
                    it,
                    SessionDetailFragmentDirections.actionSessionDetailToSpeaker(it.id)
                )
            }
        groupAdapter.update(sessionItems)

        binding.sessionVideoButton.setOnClickListener {
            session.videoUrl?.let { urlString ->
                activityActionCreator.openUrl(urlString)
            }
        }
        binding.sessionSlideButton.setOnClickListener {
            session.slideUrl?.let { urlString ->
                activityActionCreator.openUrl(urlString)
            }
        }
    }
}

@Module
abstract class SessionDetailFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @PageScope
        fun providesLifecycle(sessionsFragment: SessionDetailFragment): Lifecycle {
            return sessionsFragment.viewLifecycleOwner.lifecycle
        }
    }
}
