package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjection
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import javax.inject.Inject

class SpeakerFragment : DaggerFragment() {
    lateinit var binding: FragmentSpeakerBinding

    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_speaker, container,
            false
        )
        return binding.root
    }

    private lateinit var speakerFragmentArgs: SpeakerFragmentArgs

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        speakerFragmentArgs = SpeakerFragmentArgs.fromBundle(arguments)

        val speakerId = speakerFragmentArgs.speaker
        binding.lang = defaultLang()
        binding.timeZoneOffsetHours = 9 // FIXME Get from device setting
        sessionContentsStore.speaker(speakerId).changed(
            this
        ) { speaker ->
            binding.speaker = speaker
        }
        binding.speakerDescription.movementMethod = LinkMovementMethod.getInstance()
        sessionContentsStore.speechSessionBySpeakerName(speakerId)
            .changed(viewLifecycleOwner) { session ->
                binding.session = session
            }
        binding.speakerSessionBackground.setOnClickListener {
            navController.navigate(
                SpeakerFragmentDirections.actionSpeakerToSessionDetail(
                    binding.session?.id ?: return@setOnClickListener
                )
            )
        }
    }
}

@Module
abstract class SpeakerFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @PageScope
        fun providesLifecycle(sessionsFragment: SpeakerFragment): Lifecycle {
            return sessionsFragment.viewLifecycleOwner.lifecycle
        }

        @JvmStatic @Provides fun provideActivity(
            sessionsFragment: SpeakerFragment
        ): FragmentActivity {
            return sessionsFragment.requireActivity()
        }
    }
}
