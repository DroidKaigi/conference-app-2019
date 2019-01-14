package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.emoji.widget.EmojiTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjection
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory

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
                R.id.session_share ->
                    Toast.makeText(
                        requireContext(),
                        "not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
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
        binding.sessionFavorite.setOnClickListener {
            val session = binding.session ?: return@setOnClickListener
            sessionContentsActionCreator.toggleFavorite(session)
        }
    }

    private fun applySessionLayout(session: Session.SpeechSession) {
        binding.session = session
        // ５行以内に収まっているのならそのまま表示、6行以上だったら5行に収める
        // 「続きを読む」を押下したら全部表示する
        setEllipsisIfNeeded(binding.sessionDescription)
        binding.lang = defaultLang()
        @Suppress("StringFormatMatches") // FIXME
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            session.timeInMinutes,
            session.room.name
        )
        binding.categoryChip.text = session.category.name.getByLang(systemStore.lang)

        val sessionItems = session
            .speakers
            .map {
                speakerItemFactory.create(
                    it,
                    SessionDetailFragmentDirections.actionSessionDetailToSpeaker(it.id)
                )
            }
        groupAdapter.update(sessionItems)
    }

    private fun setEllipsisIfNeeded(textView: EmojiTextView) {
        textView.viewTreeObserver.addOnDrawListener {
            if (textView.lineCount < 6) {
                return@addOnDrawListener
            }
            val end = textView.layout.getLineStart(5)
            val ellipsis = getString(R.string.ellipsis_label)
            val text = textView.text.subSequence(0, end-ellipsis.length).toString().plus(ellipsis)
            val spannable = SpannableString(text)
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorSecondary)), text.lastIndex-ellipsis.length+1, text.lastIndex+1, Spannable.SPAN_COMPOSING)
            binding.sessionDescription.setText(spannable, TextView.BufferType.SPANNABLE)
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
