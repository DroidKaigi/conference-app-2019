package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.soywiz.klock.DateTimeSpan
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.user.store.UserStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

private const val ELIPSIS_LINE_COUNT = 5

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var userStore: UserStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory
    @Inject lateinit var activityActionCreator: ActivityActionCreator
    @Inject lateinit var navController: NavController

    private lateinit var progressTimeLatch: ProgressTimeLatch

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs
    private var showEllipsis = true

    private val groupAdapter
        get() = binding.sessionSpeakers.adapter as GroupAdapter<*>

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

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments ?: Bundle())

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionSpeakers.adapter = groupAdapter

        binding.sessionToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

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
                R.id.session_place -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener false
                    val tabIndex = if (session.room.isFirstFloor()) 0 else 1
                    navController.navigate(
                        SessionDetailFragmentDirections.actionSessionDetailToFloormap().apply {
                            setTabIndex(tabIndex)
                        }
                    )
                }
                R.id.session_calendar -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener false
                    when (session) {
                        is SpeechSession -> {
                            activityActionCreator.openCalendar(
                                session.title.getByLang(defaultLang()),
                                session.room.name,
                                session.startTime.unixMillisLong,
                                session.endTime.unixMillisLong
                            )
                        }
                        is ServiceSession -> {
                            activityActionCreator.openCalendar(
                                session.title.getByLang(defaultLang()),
                                session.room.name,
                                session.startTime.unixMillisLong,
                                session.endTime.unixMillisLong
                            )
                        }
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }

        userStore.registered.changed(viewLifecycleOwner) { registered ->
            if (registered && sessionContentsStore.isInitialized) {
                sessionContentsActionCreator.refresh()
            }
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

        binding.sessionGoToSurvey.setOnClickListener {
            val session = binding.session as? SpeechSession ?: return@setOnClickListener
            navController.navigate(
                SessionDetailFragmentDirections.actionSessionDetailToSessionSurvey(
                    session
                )
            )
        }
    }

    private fun applySpeechSessionLayout(session: SpeechSession) {
        binding.session = session
        binding.speechSession = session
        val lang = defaultLang()
        binding.lang = lang
        setupSessionDescription(session.desc)
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        binding.sessionTimeAndRoom.text = session.shortSummary()
        binding.sessionIntendedAudienceDescription.text = session.intendedAudience
        binding.categoryChip.text = session.category.name.getByLang(defaultLang())

        session.message?.let { message ->
            binding.sessionMessage.text = message.getByLang(defaultLang())
        }

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

    private fun setupSessionDescription(fullText: String) {
        val textView = binding.sessionDescription
        textView.doOnPreDraw {
            // check the number of lines if set full length text (this text is not displayed yet)
            textView.text = fullText
            // if lines are more than prescribed value then collapse
            if (textView.lineCount > ELIPSIS_LINE_COUNT && showEllipsis) {
                val lastLineStartPosition = textView.layout.getLineStart(ELIPSIS_LINE_COUNT - 1)
                val paint = textView.paint
                val ellipsis = getString(R.string.ellipsis_label)
                val ellipsisWidth = paint.measureText(ellipsis)
                // avoid shifting position, delete line feed code after target line.
                val target = fullText.substring(lastLineStartPosition).replace("\n", "")
                val lastLineText = TextUtils.ellipsize(
                    target,
                    paint,
                    textView.width - ellipsisWidth,
                    TextUtils.TruncateAt.END
                )
                val ellipsisColor = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                val onClickListener = {
                    TransitionManager.beginDelayedTransition(binding.sessionLayout)
                    textView.text = fullText
                    showEllipsis = !showEllipsis
                }
                val detailText = fullText.substring(0, lastLineStartPosition) + lastLineText
                val text = buildSpannedString {
                    clickableSpan(onClickListener, {
                        append(detailText)
                        color(ellipsisColor) {
                            append(ellipsis)
                        }
                    })
                }
                textView.setText(text, TextView.BufferType.SPANNABLE)
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun SpannableStringBuilder.clickableSpan(
        clickListener: () -> Unit,
        builderAction: SpannableStringBuilder.() -> Unit
    ) {
        inSpans(object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener()
            }

            override fun updateDrawState(ds: TextPaint) {
                // nothing
            }
        }, builderAction)
    }

    private fun applyServiceSessionLayout(session: ServiceSession) {
        binding.session = session
        binding.serviceSession = session

        val lang = defaultLang()
        binding.lang = lang
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        binding.sessionTimeAndRoom.text = session.shortSummary()
        binding.sessionDescription.text = session.desc
    }
}

@Module
abstract class SessionDetailFragmentModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PageScope
        fun providesLifecycle(sessionsFragment: SessionDetailFragment): Lifecycle {
            return sessionsFragment.viewLifecycleOwner.lifecycle
        }
    }
}
