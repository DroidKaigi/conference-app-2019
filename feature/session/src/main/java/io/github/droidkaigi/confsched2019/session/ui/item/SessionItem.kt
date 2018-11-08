package io.github.droidkaigi.confsched2019.session.ui.item

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.size
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2019.util.lazyWithParam
import kotlin.math.max

class SessionItem(
    val speechSession: Session.SpeechSession,
    private val onFavoriteClickListener: (Session.SpeechSession) -> Unit,
    private val onClickListener: (Session.SpeechSession) -> Unit,
    private val isShowDayNumber: Boolean = false,
    private val searchQuery: String = ""
) : BindableItem<ItemSessionBinding>(
    speechSession.id.toLong()
) {
    val layoutInflater by lazyWithParam<Context, LayoutInflater> { context ->
        LayoutInflater.from(context)
    }

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { onClickListener(speechSession) }
            session = speechSession
            searchQuery = searchQuery
            favorite.setOnClickListener {
                onFavoriteClickListener(speechSession)
            }
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                speechSession.timeInMinutes,
                speechSession.room.name
            )
            // TODO: Support english
            levelChip.text = speechSession.level.getNameByLang(Lang.JA)
            topicChip.text = speechSession.topic.getNameByLang(Lang.JA)

            bindSpeaker()

            speechSession.message?.let { message ->
                this@with.message.text = if (true) {
                    message.jaMessage
                } else {
                    message.enMessage
                }
            }
        }
    }

    private fun ItemSessionBinding.bindSpeaker() {
        (0 until max(speakers.size, speechSession.speakers.size)).forEach { index ->
            val existSpeakerView: TextView? = speakers.getChildAt(index) as? TextView
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
                ) as TextView
                speakerView.text = speaker.name
                speakers.addView(speakerView)
                return@forEach
            }
            if (existSpeakerView != null && speaker != null) {
                existSpeakerView.text = speaker.name
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SessionItem

        if (speechSession != other.speechSession) return false

        return true
    }

    override fun hashCode(): Int {
        return speechSession.hashCode()
    }
}
