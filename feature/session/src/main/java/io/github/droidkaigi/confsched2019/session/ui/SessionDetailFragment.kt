package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjection
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionDetailActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionDetailActionCreator: SessionDetailActionCreator
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var sessionsStore: SessionsStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory

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

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments)
        val sessionLiveData = sessionsStore.speakerSession(sessionDetailFragmentArgs.session)
        binding.sessionFavorite.setOnClickListener {
            val session = sessionLiveData.value ?: return@setOnClickListener
            sessionDetailActionCreator.toggleFavorite(session)
        }
        binding.speakers.adapter = groupAdapter
        sessionLiveData.changed(viewLifecycleOwner) { session: Session.SpeechSession ->
            binding.session = session
            @Suppress("StringFormatMatches") // FIXME
            binding.sessionTimeAndRoom.text = getString(
                R.string.session_duration_room_format,
                session.timeInMinutes,
                session.room.name
            )
            binding.sessionStartEndTime.text = buildString {
                // ex: 2月2日 10:20-10:40
                if (systemStore.lang == Lang.EN) {
                    append(session.startTime.format("M"))
                    append(".")
                    append(session.startTime.format("d"))
                } else {
                    append(session.startTime.format("M"))
                    append("月")
                    append(session.startTime.format("d"))
                    append("日")
                }
                append(" ")
                append(session.startTime.format("h:m"))
                append("-")
                append(session.endTime.format("h:m"))
            }
            binding.topicChip.text = session.topic.getNameByLang(systemStore.lang)

            val sessionItems = session
                .speakers
                .map { speakerItemFactory.create(it) }
            groupAdapter.update(sessionItems)
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

        @JvmStatic @Provides fun provideActivity(
            sessionsFragment: SessionDetailFragment
        ): FragmentActivity {
            return sessionsFragment.requireActivity()
        }
    }
}
