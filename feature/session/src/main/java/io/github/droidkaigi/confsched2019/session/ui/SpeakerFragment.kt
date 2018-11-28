package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.github.droidkaigi.confsched2019.ext.android.changedNonNull
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SpeakerActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.SpeakerStore
import me.tatarka.injectedvmprovider.ktx.injectedViewModelProvider
import javax.inject.Inject
import javax.inject.Named

class SpeakerFragment : Fragment(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var speakerActionCreator: SpeakerActionCreator

    lateinit var binding: FragmentSpeakerBinding

    @Inject lateinit var speakerStoreFactory: SpeakerStore.Factory

    private val speakerStore: SpeakerStore by lazy {
        injectedViewModelProvider
            .get(SpeakerStore::class.java.name) {
                speakerStoreFactory.create(speakerFragmentArgs.speaker)
            }
    }

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

        speakerActionCreator.load(speakerFragmentArgs.speaker)
        speakerStore.speaker.changedNonNull(
            this
        ) { speaker: Speaker ->
            binding.speaker = speaker
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
    }
}

@Module
abstract class SpeakerFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @Named("SpeakerFragment")
        fun providesLifecycle(sessionsFragment: SpeakerFragment): LifecycleOwner {
            return sessionsFragment.viewLifecycleOwner
        }

        @JvmStatic @Provides fun provideActivity(
            sessionsFragment: SpeakerFragment
        ): FragmentActivity {
            return sessionsFragment.requireActivity()
        }
    }
}
