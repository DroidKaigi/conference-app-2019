package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.soywiz.klock.DateTimeSpan
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
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
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory
    @Inject lateinit var activityActionCreator: ActivityActionCreator

    private lateinit var progressTimeLatch: ProgressTimeLatch

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs
    private val groupAdapter = GroupAdapter<ViewHolder<*>>()
    private var showEllipsis = true

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

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments)

        binding.sessionSpeakers.adapter = groupAdapter

        binding.bottomAppBar.replaceMenu(R.menu.menu_session_detail_bottomappbar)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.session_share -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener false
                    activityActionCreator.shareUrl(
                        getString(
                            R.string.session_detail_share_url,
                            session.id
                        )
                    )
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
                applySpeechSessionLayout(session)
            }

        sessionContentsStore.serviceSession(sessionDetailFragmentArgs.session)
            .changed(viewLifecycleOwner) { serviceSession ->
                applyServiceSessionLayout(serviceSession)
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

            // Immediate reflection on view to avoid time lag
            binding.sessionFavorite.setImageResource(
                if (session.isFavorited) {
                    R.drawable.ic_bookmark_border_black_24dp
                } else {
                    R.drawable.ic_bookmark_black_24dp
                }
            )

            // Animation
            it.scaleX = 0.8f
            it.scaleY = 0.8f
            it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300)
                .interpolator = OvershootInterpolator()

            sessionContentsActionCreator.toggleFavorite(session)
        }
    }

    private fun applySpeechSessionLayout(session: Session.SpeechSession) {
        binding.session = session
        binding.speechSession = session
        val lang = defaultLang()
        binding.lang = lang
        drawSessionDescription()
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        @Suppress("StringFormatMatches") // FIXME
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            session.timeInMinutes,
            session.room.name
        )
        binding.sessionIntendedAudienceDescription.text = session.intendedAudience
        binding.categoryChip.text = session.category.name.getByLang(defaultLang())

        session.message?.let { message ->
            binding.sessionMessage.text = message.getByLang(defaultLang())
        }

        binding.sessionDescription.text = session.desc

        val speakerItems = session
            .speakers
            .map {
                speakerItemFactory.create(
                    it,
                    SessionDetailFragmentDirections.actionSessionDetailToSpeaker(it.id)
                )
            }

        groupAdapter.update(speakerItems)

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

    private fun drawSessionDescription() {
        val textView = binding.sessionDescription
        textView.viewTreeObserver.addOnDrawListener {
            if (textView.lineCount > 5) {
                if (showEllipsis) {
                    val end = textView.layout.getLineStart(5)
                    val ellipsis = getString(R.string.ellipsis_label)
                    val text = textView.text.subSequence(0, end-ellipsis.length).toString().plus(ellipsis)
                    val spannable = SpannableString(text).apply {
                        setSpan(
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    val session = binding.speechSession?.desc
                                    binding.sessionDescription.text = session
                                    showEllipsis = !showEllipsis
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                    ds.color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                                    ds.isUnderlineText = false
                                }
                            },
                            text.lastIndex-ellipsis.length+1,
                            text.lastIndex+1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    binding.sessionDescription.setText(spannable, TextView.BufferType.SPANNABLE)
                    binding.sessionDescription.movementMethod = LinkMovementMethod.getInstance()
                }
            }
        }
    }

    private fun applyServiceSessionLayout(session: Session.ServiceSession) {
        binding.session = session
        binding.serviceSession = session

        val lang = defaultLang()
        binding.lang = lang
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        @Suppress("StringFormatMatches") // FIXME
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            session.timeInMinutes,
            session.room.name
        )

        binding.sessionDescription.text = session.desc
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
