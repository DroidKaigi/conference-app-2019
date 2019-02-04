package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import android.transition.Fade
import com.soywiz.klock.DateTimeSpan
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2019.session.ui.bindingadapter.ImageLoadListener
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

        speakerFragmentArgs = SpeakerFragmentArgs.fromBundle(arguments ?: Bundle())

        val speakerId = speakerFragmentArgs.speaker
        binding.lang = defaultLang()
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting
        binding.listener = object : ImageLoadListener {
            override fun onImageLoaded() {
                startPostponedEnterTransition()
            }

            override fun onImageLoadFailed() {
                startPostponedEnterTransition()
            }
        }
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
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.speaker_shared_enter)
        enterTransition = Fade()
        returnTransition = null
        postponeEnterTransition()
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
