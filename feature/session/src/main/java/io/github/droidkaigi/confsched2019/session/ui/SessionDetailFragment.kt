package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changedNonNull
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionDetailActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionDetailStore
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import me.tatarka.injectedvmprovider.ktx.injectedViewModelProvider
import javax.inject.Inject

class SessionDetailFragment : Fragment(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var sessionDetailActionCreator: SessionDetailActionCreator
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory

    lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionDetailStoreFactory: SessionDetailStore.Factory
    private val sessionDetailStore: SessionDetailStore by lazy {
        injectedViewModelProvider
            .get(SessionDetailStore::class.java.name) {
                sessionDetailStoreFactory.create(sessionDetailFragmentArgs.session)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_session_detail, container,
            false
        )
        return binding.root
    }

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments)
        binding.favorite.setOnClickListener {
            val session = sessionDetailStore.session.value ?: return@setOnClickListener
            sessionDetailActionCreator.toggleFavorite(session)
        }
        sessionDetailActionCreator.load(sessionDetailFragmentArgs.session)
        sessionDetailStore.session.changedNonNull(this) { session: Session.SpeechSession ->
            binding.session = session
            @Suppress("StringFormatMatches") // FIXME
            binding.timeAndRoom.text = getString(
                R.string.session_duration_room_format,
                session.timeInMinutes,
                session.room.name
            )
            binding.startEndTime.text = buildString {
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

            binding.speakers.adapter = GroupAdapter<ViewHolder<*>>().apply {

                update(
                    session.speakers.map { speakerItemFactory.create(it) }
                )
            }
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
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
