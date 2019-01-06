package io.github.droidkaigi.confsched2019.session.ui.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.bindingadapter.loadImage
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import io.github.droidkaigi.confsched2019.util.lazyWithParam
import kotlin.math.max

class SpeechSessionItem @AssistedInject constructor(
    @Assisted override val session: Session.SpeechSession,
    @Assisted val navDirections: NavDirections,
    navController: NavController,
    sessionContentsActionCreator: SessionContentsActionCreator,
    val systemStore: SystemStore
) : BindableItem<ItemSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem {
    val speechSession get() = session

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session.SpeechSession,
            navDirections: NavDirections
        ): SpeechSessionItem
    }

    private val onFavoriteClickListener: (Session.SpeechSession) -> Unit = { session ->
        sessionContentsActionCreator.toggleFavorite(session)
    }
    private val onClickListener: (Session.SpeechSession) -> Unit = { session ->
        navController
            .navigate(
                navDirections
            )
    }
    val layoutInflater by lazyWithParam<Context, LayoutInflater> { context ->
        LayoutInflater.from(context)
    }

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { onClickListener(speechSession) }
            session = speechSession
            lang = defaultLang()
            favorite.setOnClickListener {
                onFavoriteClickListener(speechSession)
            }
            @Suppress("StringFormatMatches") // FIXME
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                speechSession.timeInMinutes,
                speechSession.room.name
            )
            categoryChip.text = speechSession.category.name.getByLang(systemStore.lang)

            bindSpeaker()

            speechSession.message?.let { message ->
                this@with.message.text = message.getBodyByLang(systemStore.lang)
            }
        }
    }

    private fun ItemSessionBinding.bindSpeaker() {
        (0 until max(
            speakers.size, speechSession.speakers.size
        )).forEach { index ->
            val existSpeakerView = speakers.getChildAt(index) as? ViewGroup
            val speaker: Speaker? = speechSession.speakers.getOrNull(index)
            if (existSpeakerView == null && speaker == null) {
                return@forEach
            }
            if (existSpeakerView != null && speaker == null) {
                // Cache for performance
                existSpeakerView.isVisible = false
                return@forEach
            }
            if (existSpeakerView == null && speaker != null) {
                val speakerView = layoutInflater.get(root.context).inflate(
                    R.layout.layout_speaker, speakers, false
                ) as ViewGroup
                val imageView: ImageView = speakerView.findViewById(
                    R.id.speaker_image
                )
                val textView: TextView = speakerView.findViewById(R.id.speaker)
                bindSpeakerData(speaker, textView, imageView)

                speakers.addView(speakerView)
                return@forEach
            }
            if (existSpeakerView != null && speaker != null) {
                val textView: TextView = existSpeakerView.findViewById(R.id.speaker)
                textView.text = speaker.name
                val imageView = existSpeakerView.findViewById<ImageView>(R.id.speaker_image)
                bindSpeakerData(speaker, textView, imageView)
            }
        }
    }

    private fun bindSpeakerData(
        speaker: Speaker,
        textView: TextView,
        imageView: ImageView
    ) {
        textView.text = speaker.name
        val context = imageView.context
        val placeHolder = VectorDrawableCompat.create(
            context.resources,
            R.drawable.ic_person_outline_black_24dp,
            null
        )
        val placeHolderColor = ContextCompat.getColor(
            context,
            R.color.gray2
        )
        loadImage(
            imageView = imageView,
            imageUrl = speaker.imageUrl,
            circleCrop = true,
            rawPlaceHolder = placeHolder,
            placeHolderTint = placeHolderColor
        )
    }

    override fun getLayout(): Int = R.layout.item_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpeechSessionItem

        if (session != other.session) return false

        return true
    }

    override fun hashCode(): Int {
        return session.hashCode()
    }
}
